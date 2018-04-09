package com.kron.fluentdsample.server;

import com.kron.fluentsample.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class TagLoggingServer {
    private static final Logger logger = Logger.getLogger(TagLoggingServer.class.getName());
    private Server server;
    private ITagStreamCallback tagStreamCallback;
    private ITransPortFilterCallback transPortFilterCallback;
    private IAntennaHealthCheckCallback antennaHealthCheckCallback;
    private List<String> ids;

    public TagLoggingServer(ITagStreamCallback tagStreamCallback, ITransPortFilterCallback transPortFilterCallback, IAntennaHealthCheckCallback antennaHealthCheckCallback) {
        this.tagStreamCallback = tagStreamCallback;
        this.transPortFilterCallback = transPortFilterCallback;
        this.antennaHealthCheckCallback = antennaHealthCheckCallback;
        this.ids = new ArrayList<>();
    }

    public void addId(String id){
        ids.add(id);
    }

    public void start() throws IOException {
        int port = 50051;

        server = ServerBuilder.forPort(port)
                .addService(new TagLoggingServiceImpl())
                .addTransportFilter(new TransPortFilterImpl(transPortFilterCallback))
                .build()
                .start();
        logger.info("Server started, listening on " + port);


        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                TagLoggingServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private class TagLoggingServiceImpl extends TagLoggingServiceGrpc.TagLoggingServiceImplBase {
        @Override
        public void serverList(NoParams request, StreamObserver<ServerListResponse> responseObserver) {
            ServerListResponse reply = ServerListResponse.newBuilder()
                    .addAllServerList(ids)
                    .build();

            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void antennaHealthCheck(NoParams request, StreamObserver<AntennaChange> responseObserver) {
            antennaHealthCheckCallback.call(request, responseObserver);
        }

        @Override
        public StreamObserver<ReaderIDRequest> tagStream(StreamObserver<TagReport> responseObserver) {
            Map<String, ReaderIDRequest> requestMap = new HashMap<>();

            return new StreamObserver<ReaderIDRequest>() {
                @Override
                public void onNext(ReaderIDRequest request) {
                    tagStreamCallback.call(request.getId(), responseObserver);
                    if(requestMap.containsKey(request.getId())){
                        System.out.println("Already exists request");
                    }
                    requestMap.put(request.getId(), request);
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };
        }
    }
}

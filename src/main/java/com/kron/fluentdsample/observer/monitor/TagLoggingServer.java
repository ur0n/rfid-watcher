package com.kron.fluentdsample.observer.monitor;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.ServerCalls;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class TagLoggingServer {
    private static final Logger logger = Logger.getLogger(TagLoggingServer.class.getName());
    private Server server;
    private ITagStreamCallback tagStreamCallback;
    private ITransPortFilterCallback transPortFilterCallback;
    private List<String> ips;

    public TagLoggingServer(ITagStreamCallback tagStreamCallback, ITransPortFilterCallback transPortFilterCallback) {
        this.tagStreamCallback = tagStreamCallback;
        this.transPortFilterCallback = transPortFilterCallback;
        ips = Collections.singletonList("0.0.0.0");
    }

    public TagLoggingServer(List<String> ips, ITagStreamCallback tagStreamCallback, ITransPortFilterCallback transPortFilterCallback) {
        this.ips = ips;
        this.tagStreamCallback = tagStreamCallback;
        this.transPortFilterCallback = transPortFilterCallback;
    }

    public void start() throws IOException {
        int port = 50051;

        server = ServerBuilder.forPort(port)
                .addService(new TagLoggingServiceImpl(tagStreamCallback, ips))
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
}

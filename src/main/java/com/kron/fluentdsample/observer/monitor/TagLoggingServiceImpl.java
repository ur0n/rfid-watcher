package com.kron.fluentdsample.observer.monitor;

import com.kron.fluentsample.*;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class TagLoggingServiceImpl extends TagLoggingServiceGrpc.TagLoggingServiceImplBase {
    private ITagStreamCallback callback;
    private List<String> ips;

    TagLoggingServiceImpl(ITagStreamCallback callback, List<String> ips) {
        this.callback = callback;
        this.ips = ips;
    }

    @Override
    public void serverList(NoParams request, StreamObserver<ServerListResponse> responseObserver) {
        ServerListResponse reply = ServerListResponse.newBuilder()
                .addAllServerList(ips)
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void tagStream(ReaderIDRequest request, StreamObserver<TagReport> responseObserver) {
        callback.call(request.getId(), responseObserver);
    }

    @Override
    public void antennaHealthCheck(NoParams request, StreamObserver<AntennaChange> responseObserver) {

    }
}

package com.kron.fluentdsample.server;

import com.kron.fluentsample.TagReport;
import io.grpc.stub.StreamObserver;

public interface ITagStreamCallback {
    void call(String id, StreamObserver<TagReport> response);
}

package com.kron.fluentdsample;

import com.kron.fluentdsample.observer.monitor.ITagStreamCallback;
import com.kron.fluentdsample.observer.monitor.ITransPortFilterCallback;
import com.kron.fluentdsample.observer.monitor.TagLoggingServer;
import com.kron.fluentsample.TagReport;
import io.grpc.Attributes;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class GrpcServer extends Thread implements ITagStreamCallback, ITransPortFilterCallback {
    private TagLoggingServer server;

    GrpcServer() {
        this.server = new TagLoggingServer(this::call, this::call);
    }

    @Override
    public void call(String id, StreamObserver<TagReport> response) {
        TagReport reply = TagReport.newBuilder().setId("1").build();

        int i = 0;
        while (true) {
            response.onNext(reply);
            if(i == -1) break;
            i++;
        }
        response.onCompleted();
    }

    @Override
    public void call(Attributes attributes) {

    }

    public void run() {
        try {
            server.start();
            server.blockUntilShutdown();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GrpcServer().start();
    }
}

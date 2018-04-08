package com.kron.fluentdsample;

import com.kron.fluentdsample.server.TagLoggingServer;

import java.io.IOException;

public class GrpcServerRunner extends Thread {
    private TagLoggingServer server;

    public GrpcServerRunner(TagLoggingServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            server.start();
            server.blockUntilShutdown();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

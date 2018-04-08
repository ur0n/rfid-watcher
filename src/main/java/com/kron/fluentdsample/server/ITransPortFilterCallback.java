package com.kron.fluentdsample.server;

import io.grpc.Attributes;

public interface ITransPortFilterCallback {
    void call(Attributes attributes);
}

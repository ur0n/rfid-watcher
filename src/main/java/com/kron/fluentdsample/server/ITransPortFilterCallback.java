package com.kron.fluentdsample.observer.monitor;

import io.grpc.Attributes;

public interface ITransPortFilterCallback {
    void call(Attributes attributes);
}

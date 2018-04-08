package com.kron.fluentdsample.observer.monitor;

import com.kron.fluentsample.AntennaChange;
import com.kron.fluentsample.NoParams;
import io.grpc.stub.StreamObserver;

public interface IAntennaHealthCheckCallback {
    void call(NoParams noParams, StreamObserver<AntennaChange> response);
}

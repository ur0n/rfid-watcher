package com.kron.fluentdsample.observer.monitor;

import com.kron.fluentdsample.entity.AntennaHealth;

public interface IAntennaHealthChangeCallback {
    void call(AntennaHealth AntennaHealth);
}

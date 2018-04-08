package com.kron.fluentdsample.observer.monitor;

import com.kron.fluentdsample.entity.AntennaHealth;
import com.kron.fluentdsample.observer.Observer;
import com.kron.fluentdsample.reporter.Reporter;

public class AntennaHealthObserver implements Observer<AntennaHealth> {
    private IAntennaHealthChangeCallback callback;

    public AntennaHealthObserver(IAntennaHealthChangeCallback callback){
        this.callback = callback;
    }

    @Override
    public void update(Reporter<AntennaHealth> reporter) {
        AntennaHealth antennaHealth = reporter.getValue();
        callback.call(antennaHealth);
    }
}

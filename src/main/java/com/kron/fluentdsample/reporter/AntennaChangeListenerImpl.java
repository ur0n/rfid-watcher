package com.kron.fluentdsample;

import com.impinj.octane.AntennaChangeListener;
import com.impinj.octane.AntennaEvent;
import com.impinj.octane.ImpinjReader;
import com.kron.fluentdsample.reporter.Reporter;

public class AntennaChangeListenerImpl extends Reporter<AntennaHealth> implements AntennaChangeListener {
    private AntennaHealth antennaHealth;

    @Override
    public AntennaHealth getValue() {
        return antennaHealth;
    }

    @Override
    public void execute() {

    }

    @Override
    public void onAntennaChanged(ImpinjReader impinjReader, AntennaEvent antennaEvent) {
        antennaHealth = new AntennaHealth(impinjReader.getAddress(), antennaEvent.getPortNumber(), antennaEvent.getState().getValue());
        notifyObservers();
    }
}
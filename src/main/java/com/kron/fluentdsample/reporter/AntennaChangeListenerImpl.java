package com.kron.fluentdsample.reporter;

import com.impinj.octane.AntennaChangeListener;
import com.impinj.octane.AntennaEvent;
import com.impinj.octane.ImpinjReader;
import com.kron.fluentdsample.entity.AntennaHealth;

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
        System.out.println("=============== Antenna Change ===================");
        System.out.println(antennaHealth);
        System.out.println("==================================================");
        notifyObservers();
    }
}
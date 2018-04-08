package com.kron.fluentdsample.reporter;

import com.kron.fluentdsample.entity.AntennaHealth;

public class AntennaHealthReporter extends Reporter<AntennaHealth> {
    private AntennaHealth antennaHealth;
    private RFIDReader reader;

    public AntennaHealthReporter(RFIDReader reader) {
        this.reader = reader;
    }

    @Override
    public AntennaHealth getValue() {
        return antennaHealth;
    }

    public void update(AntennaHealth antennaHealth){
        this.antennaHealth = antennaHealth;
        notifyObservers();
    }

    @Override
    public void execute() {

    }
}

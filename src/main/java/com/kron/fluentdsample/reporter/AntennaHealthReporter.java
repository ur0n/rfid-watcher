package com.kron.fluentdsample.reporter;

import com.kron.fluentdsample.AntennaHealth;

public class AntennaHealthReporter extends Reporter<AntennaHealth> {
    private AntennaHealth antennaHealth;

    @Override
    public AntennaHealth getValue() {
        return antennaHealth;
    }

    @Override
    public void execute() {

    }
}

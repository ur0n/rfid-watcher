package com.kron.fluentdsample.reporter;

import com.impinj.octane.*;

import java.util.ArrayList;

public class RFIDReader {
    private ImpinjReader reader;
    private String ip;
    private Settings settings;
    private AntennaConfigGroup acg;
    private TagReportListener tagReportListener;
    private AntennaChangeListener antennaChangeListener;

    public RFIDReader(String ip) {
        this.ip = ip;
        this.reader = new ImpinjReader(ip, "RFIDReade" + ip);
    }

    public void connect() throws OctaneSdkException {
        reader.connect(ip);
    }

//    public void setting() {
//        settingReport();
//        settingAntenna();
//    }

    public void setting(ArrayList<Double> freqList) {
        this.settingReport(freqList);
        this.settingAntenna();
    }

    public void setTagReportListener(TagReportListener tagReportListener) {
        this.tagReportListener = tagReportListener;
        reader.setTagReportListener(tagReportListener);
    }

    public void setAntennaChangeListener(AntennaChangeListener antennaChangeListener){
        this.antennaChangeListener = antennaChangeListener;
        reader.setAntennaChangeListener(antennaChangeListener);
    }

    private void settingAntenna() {
        acg = settings.getAntennas();
    }


//    private void settingReport() {
//        settings = reader.queryDefaultSettings();
//        settings.getReport().setIncludeAntennaPortNumber(true);
//        settings.getReport().setMode(ReportMode.Individual);
//        settings.getReport().setIncludePeakRssi(true);
//        settings.getReport().setIncludeLastSeenTime(true);
//        settings.getReport().setIncludePhaseAngle(true);
//        settings.getReport().setIncludeDopplerFrequency(true);
//
//
//        try {
//            reader.applySettings(settings);
//        } catch (OctaneSdkException e) {
//            e.printStackTrace();
//        }
//    }

    private void settingReport(ArrayList<Double> freqList) {
        settings = reader.queryDefaultSettings();
        settings.getReport().setIncludeAntennaPortNumber(true);
        settings.getReport().setMode(ReportMode.Individual);
        settings.getReport().setIncludePeakRssi(true);
        settings.getReport().setIncludeLastSeenTime(true);
        settings.getReport().setIncludePhaseAngle(true);
        settings.getReport().setIncludeDopplerFrequency(true);

        // add abe
        settings.setTxFrequenciesInMhz(freqList);

        try {
            reader.applySettings(settings);
        } catch (OctaneSdkException e) {
            e.printStackTrace();
        }
    }



    public AntennaConfigGroup getAcg() {
        return acg;
    }

    public void start() throws OctaneSdkException {
        // Start the reader
        reader.start();
    }

    public Settings getSettings(){ return settings; }
}

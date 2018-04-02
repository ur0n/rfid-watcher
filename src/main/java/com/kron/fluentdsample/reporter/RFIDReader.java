package com.kron.fluentdsample.reporter;

import com.impinj.octane.*;
import com.kron.fluentdsample.TagData;

import java.util.List;

public class RFIDReader {
    private ImpinjReader reader;
    private String ip;
    private TagDataReporter reporter;

    RFIDReader(String ip) {
        this.ip = ip;
    }

    public void connect() {
        try {
            reader.connect(ip);
        } catch (OctaneSdkException e) {
            e.printStackTrace();
        }
    }

    public void settingReader() {
        Settings settings = reader.queryDefaultSettings();
        settings.getReport().setIncludeAntennaPortNumber(true);
        settings.getReport().setMode(ReportMode.Individual);
        settings.getReport().setIncludePeakRssi(true);
        settings.getReport().setIncludeLastSeenTime(true);
        settings.getReport().setIncludePhaseAngle(true);
        settings.getReport().setIncludeDopplerFrequency(true);

        try {
            reader.applySettings(settings);
        } catch (OctaneSdkException e) {
            e.printStackTrace();
        }
    }

    public void setListeners() {
        reader.setTagReportListener(new TagReportListenerImpl());
    }

    public void start() {
        try {
            // Start the reader
            reader.start();
        } catch (OctaneSdkException e) {
            e.printStackTrace();
        }
    }

    private void notifyReporter(com.kron.fluentdsample.TagData tagData) {
        reporter.update(tagData);
    }

    private class AntennaChangeListenerImpl implements AntennaChangeListener {
        @Override
        public void onAntennaChanged(ImpinjReader impinjReader, AntennaEvent antennaEvent) {

        }
    }

    private class TagReportListenerImpl implements TagReportListener {
        @Override
        public void onTagReported(ImpinjReader reader, TagReport report) {
            List<Tag> tags = report.getTags();
            tags.forEach(tag -> {

                int port = tag.getAntennaPortNumber();
                String id = String.join("", tag.getEpc().toString().split(" "));
                double rssi = tag.getPeakRssiInDbm();
                long time = Long.valueOf(tag.getLastSeenTime().ToString());
                double phase = tag.getPhaseAngleInRadians();
                com.kron.fluentdsample.TagData tagdata = new TagData(ip, port, id, rssi, time, phase);
                notifyReporter(tagdata);
            });
        }
    }
}

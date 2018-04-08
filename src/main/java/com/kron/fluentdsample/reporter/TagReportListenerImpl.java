package com.kron.fluentdsample;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.Tag;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;
import com.kron.fluentdsample.observer.Observer;
import com.kron.fluentdsample.reporter.Reporter;

import java.util.List;

public class TagReportListenerImpl extends Reporter<TagData> implements TagReportListener {
    private TagData tagData;

    @Override
    public TagData getValue() {
        return null;
    }

    @Override
    public void execute() {

    }

    @Override
    public void onTagReported(ImpinjReader impinjReader, TagReport report) {
        List<Tag> tags = report.getTags();
        tags.forEach(tag -> {

            int port = tag.getAntennaPortNumber();
            String id = String.join("", tag.getEpc().toString().split(" "));
            double rssi = tag.getPeakRssiInDbm();
            long time = Long.valueOf(tag.getLastSeenTime().ToString());
            double phase = tag.getPhaseAngleInRadians();
            tagData = new TagData(impinjReader.getAddress(), port, id, rssi, time, phase);
            notifyObservers();
        });
    }
}

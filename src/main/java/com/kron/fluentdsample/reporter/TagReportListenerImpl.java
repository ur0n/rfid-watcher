package com.kron.fluentdsample.reporter;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.Tag;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;
import com.kron.fluentdsample.entity.TagData;

import java.util.List;

public class TagReportListenerImpl extends Reporter<TagData> implements TagReportListener {
    private TagData tagData;

    @Override
    public TagData getValue() {
        return tagData;
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
//            long time = Long.valueOf(tag.getLastSeenTime().ToString());
            long time = System.currentTimeMillis();
            double phase = tag.getPhaseAngleInRadians();
            double dopplerFrequency = tag.getRfDopplerFrequency();
            tagData = new TagData(impinjReader.getAddress(), port, id, rssi, time, phase, dopplerFrequency);
            notifyObservers();
        });
    }
}

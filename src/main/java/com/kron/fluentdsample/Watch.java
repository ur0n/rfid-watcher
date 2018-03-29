package com.kron.fluentdsample;

import com.impinj.octane.*;
import org.fluentd.logger.FluentLogger;
import java.text.SimpleDateFormat;
import java.util.*;

public class Watch {
    private String ip;
    private FluentLogger logger = FluentLogger.getLogger("tag", "fluentd", 24224);

    Watch(String ip) {
        this.ip = ip;
    }

    private Settings settingReportInformation(Settings settings) {
        settings.getReport().setIncludeAntennaPortNumber(true);
        settings.getReport().setMode(ReportMode.Individual);
        settings.getReport().setIncludePeakRssi(true);
        settings.getReport().setIncludeLastSeenTime(true);
        settings.getReport().setIncludePhaseAngle(true);
        settings.getReport().setIncludeDopplerFrequency(true);
        return settings;
    }

    public void execute() {
        try {
            ImpinjReader reader = new ImpinjReader();

            // Connect
            System.out.println("Connecting to " + ip);
            reader.connect(ip);

            // Get the default settings
            System.out.println("Setting Reader");
            Settings settings = reader.queryDefaultSettings();
            settings = settingReportInformation(settings);

            // Apply the new settings
            reader.applySettings(settings);

            System.out.println("Set TagReportListener");
            reader.setTagReportListener(
                    new TagReportListenerImpl()
            );

            // Start the reader
            reader.start();

            System.out.println("Press Enter to stop.");

            Scanner s = new Scanner(System.in);
            s.nextLine();

            System.out.println("Done");
        } catch (OctaneSdkException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace(System.out);
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
                TagData tagdata = new TagData(ip, port, id, rssi, time, phase);

                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                String timeTag = format.format(new Date(time / 1000));

                logger.log("report" + timeTag + ip + port, tagdata.toMap());
                System.out.println(tagdata.toString());
            });
        }
    }
}

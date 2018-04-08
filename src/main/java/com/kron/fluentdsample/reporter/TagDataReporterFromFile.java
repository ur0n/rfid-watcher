package com.kron.fluentdsample.reporter;

import com.kron.fluentdsample.entity.TagData;
import com.kron.fluentdsample.TagReportingServer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class TagDataReporterFromFile extends Reporter<TagData> {
    private TagData tagData;

    @Override
    public TagData getValue() {
        return tagData;
    }

    @Override
    public void execute() {
        URL url = TagReportingServer.class.getResource("/pitcher_farside00.csv");

        try {
            Path path = Paths.get(url.toURI());
            Random rand = new Random();
            Files.lines(path, StandardCharsets.UTF_8).forEach((String line) -> {
                String[] splited = line.split(",");
//                int port = Integer.parseInt(splited[0]);
                int port = rand.nextInt(4) + 1;
                String id = String.join("", splited[1].split(" "));
                String ip = "0.0.0.0." + rand.nextInt(4);
                double rssi = Double.parseDouble(splited[2]);
                long time = Long.valueOf(splited[4]);
                double phase = Double.parseDouble(splited[3]);
                tagData = new TagData(ip, port, id, rssi, time, phase);
                notifyObservers();

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}

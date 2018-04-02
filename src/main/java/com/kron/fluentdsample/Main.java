package com.kron.fluentdsample;

import com.kron.fluentdsample.observer.fluentd.FluentObserver;
import com.kron.fluentdsample.observer.monitor.*;
import com.kron.fluentdsample.reporter.Reporter;
import com.kron.fluentdsample.reporter.TagDataReporter;
import com.kron.fluentsample.TagReport;
import io.grpc.Attributes;
import io.grpc.stub.StreamObserver;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main implements ITagStreamCallback, ITransPortFilterCallback ,ICallback  {
    List<String> ips;
    private List<Reporter> jobs;
    private List<StreamObserver<TagReport>> responses;

    @Override
    public void call(String id, StreamObserver<TagReport> response) {
        responses.set(Integer.parseInt(id), response);
    }

    @Override
    public void call(Attributes attributes) {

    }

    @Override
    public void call(TagData tagData) {
        int id = ips.indexOf(tagData.getIp());
        if (id != -1) {
            StreamObserver<TagReport> response = responses.get(id);
            response.onNext(tagData.toTagReport());
            response.onCompleted();
        }
    }

    private void readYAML() {
        String root = System.getProperty("user.dir");
        final Yaml y = new Yaml();

        try (final InputStream is = Files.newInputStream(Paths.get(root + "/config/readers.yml"))) {
            ips = y.loadAs(is, List.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        readYAML();

        TagLoggingServer server = new TagLoggingServer(ips, this::call, this::call);
        GrpcServerRunner runner = new GrpcServerRunner(server);
        runner.start();

        ips.parallelStream().forEach(ip -> {
            TagDataReporter reporter = new TagDataReporter(ip);
            FluentObserver fluentObserver = new FluentObserver();
            MonitorObserver monitorObserver = new MonitorObserver();

            reporter.addObserver(fluentObserver);
            reporter.addObserver(monitorObserver);
            jobs.add(reporter);
            reporter.execute();
        });
    }

    public static void main(String[] args) {
        new Main().execute();
    }
}

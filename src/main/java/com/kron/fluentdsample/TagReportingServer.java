package com.kron.fluentdsample;

import com.kron.fluentdsample.observer.fluentd.FluentObserver;
import com.kron.fluentdsample.observer.monitor.*;
import com.kron.fluentdsample.reporter.Reporter;
import com.kron.fluentdsample.reporter.TagDataReporterFromFile;
import com.kron.fluentsample.TagReport;
import io.grpc.Attributes;
import io.grpc.stub.StreamObserver;

import java.util.Collections;
import java.util.List;

public class TagReportingServer implements ITagStreamCallback, ITransPortFilterCallback, ICallback {
    private StreamObserver<TagReport> response;
    private Reporter reporter;
    private MonitorObserver monitorObserver;

    @Override
    public void call(TagData tagData) {
        response.onNext(tagData.toTagReport());
    }

    @Override
    public void call(String id, StreamObserver<TagReport> response) {
        this.response = response;
        monitorObserver.setCallback(this::call);
        reporter.updateObserver(monitorObserver);
    }

    @Override
    public void call(Attributes attributes) {
        monitorObserver.resetCallback();
        reporter.updateObserver(monitorObserver);
    }

    public void execute() {
        List<String> ips = Collections.singletonList("0.0.0.0");
        TagLoggingServer server = new TagLoggingServer(ips, this::call, this::call);
        GrpcServerRunner runner = new GrpcServerRunner(server);
        runner.start();

        reporter = new TagDataReporterFromFile();
        monitorObserver = new MonitorObserver();

        reporter.addObserver(new FluentObserver());
        reporter.addObserver(monitorObserver);
        reporter.execute();
    }

    public static void main(String[] args) {
        new TagReportingServer().execute();
    }

}

package com.kron.fluentdsample;

import com.kron.fluentdsample.entity.AntennaHealth;
import com.kron.fluentdsample.entity.TagData;
import com.kron.fluentdsample.observer.monitor.*;
import com.kron.fluentdsample.reporter.Reporter;
import com.kron.fluentdsample.reporter.TagDataReporterFromFile;
import com.kron.fluentdsample.server.*;
import com.kron.fluentsample.AntennaChange;
import com.kron.fluentsample.GetAllReportRequest;
import com.kron.fluentsample.NoParams;
import com.kron.fluentsample.TagReport;
import io.grpc.Attributes;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TagReportingServer implements ITagAllStreamCallback, IAntennaHealthCheckCallback, ITransPortFilterCallback, ICallback {

    private StreamObserver<TagReport> response;
    private Reporter reporter;
    private MonitorObserver monitorObserver;

    @Override
    public void call(TagData tagData) {
        response.onNext(tagData.toTagReport());
    }

    @Override
    public void call(GetAllReportRequest getAllReportRequest, StreamObserver<TagReport> response) {
        this.response = response;
        monitorObserver.setCallback(this::call);
        reporter.updateObserver(monitorObserver);
    }

    @Override
    public void call(NoParams noParams, StreamObserver<AntennaChange> response) {
        Thread t = new Thread(new HealthChange(response));
        t.start();
    }

    private class HealthChange implements Runnable {
        private StreamObserver<AntennaChange> antennaChangeResponse;

        HealthChange(StreamObserver<AntennaChange> antennaChangeResponse) {
            this.antennaChangeResponse = antennaChangeResponse;
        }

        @Override
        public void run() {
            Random rand = new Random();
            while (true) {
                AntennaHealth antennaHealth = new AntennaHealth("0.0.0." + rand.nextInt(4), rand.nextInt(4) + 1, rand.nextInt(2));
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(antennaHealth.toString());
                antennaChangeResponse.onNext(antennaHealth.toAntennaChange());
            }
        }

    }

    @Override
    public void call(Attributes attributes) {
        System.out.println(attributes.toString());
        monitorObserver.resetCallback();
        reporter.updateObserver(monitorObserver);
    }

    public void execute() {
        TagLoggingServer server = new TagLoggingServer(this::call, this::call, this::call);

        List<String> ips = Arrays.asList(
                "0.0.0.0",
                "0.0.0.1",
                "0.0.0.2"
        );

        ips.forEach(System.out::println);

        List<String> numbers = IntStream.range(1, 5).boxed().map(Object::toString).collect(Collectors.toList());

        ips.forEach(ip -> {
            numbers.forEach(number -> {
                System.out.println(ip + ":" + number);
                server.addId(ip + ":" + number);
            });
        });

        GrpcServerRunner runner = new GrpcServerRunner(server);
        runner.start();

        reporter = new TagDataReporterFromFile();
        monitorObserver = new MonitorObserver();

        reporter.addObserver(monitorObserver);
        reporter.execute();
    }

    public static void main(String[] args) {
        new TagReportingServer().execute();
    }

}

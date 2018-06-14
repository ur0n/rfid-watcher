package com.kron.fluentdsample;

import com.impinj.octane.AntennaConfig;
import com.impinj.octane.OctaneSdkException;
import com.kron.fluentdsample.entity.AntennaHealth;
import com.kron.fluentdsample.entity.TagData;
import com.kron.fluentdsample.observer.fluentd.FluentObserver;
import com.kron.fluentdsample.observer.monitor.*;
import com.kron.fluentdsample.observer.redis.RedisObserver;
import com.kron.fluentdsample.reporter.AntennaChangeListenerImpl;
import com.kron.fluentdsample.reporter.RFIDReader;
import com.kron.fluentdsample.reporter.Reporter;
import com.kron.fluentdsample.reporter.TagReportListenerImpl;
import com.kron.fluentdsample.server.*;
import com.kron.fluentsample.AntennaChange;
import com.kron.fluentsample.NoParams;
import com.kron.fluentsample.TagReport;
import io.grpc.Attributes;
import io.grpc.stub.StreamObserver;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main implements ITagStreamCallback, ITransPortFilterCallback, IAntennaHealthCheckCallback, IAntennaHealthChangeCallback, ICallback {
    private List<String> ips;
    private StreamObserver<TagReport> response;
    private String id;
    private MonitorObserver monitorObserver;

    private Map<String, Reporter<TagData>> reporterMap;
    private Map<String, MonitorObserver> monitorObserverMap;
    private StreamObserver<AntennaChange> antennaChangeStreamObserver;

    Main() {
        this.ips = new ArrayList<>();
        this.reporterMap = new HashMap<>();
        this.monitorObserverMap = new HashMap<>();
    }

    /*
        id: 192.168.0.102:1
        ip:portのようになっている
    */
    // TODO 他のidがきたら前回のオブサーバーをリセットする
    @Override
    public void call(String id, StreamObserver<TagReport> response) {
        String ip = id.split(":")[0];
        MonitorObserver observer = monitorObserverMap.get(id);

        // 前回のオブサーバーをリセットする
        if (this.id != null && !id.equals(this.id)) {
            String prevIp = this.id.split(":")[0];
            monitorObserver.resetCallback();
            reporterMap.get(prevIp).updateObserver(monitorObserver);
        }

        this.id = id;
        this.response = response;
        observer.setCallback(this::call);
        reporterMap.get(ip).updateObserver(observer);
        monitorObserver = observer;
    }

    @Override
    public void call(NoParams noParams, StreamObserver<AntennaChange> response) {
        antennaChangeStreamObserver = response;
    }

    // reporterに登録されているmonitorObserverを全て初期化する
    // MonitorObserver.resetCallback()を呼ぶと何もしないコールバックがセットされる
    @Override
    public void call(Attributes attributes) {
        // keySet() == Set[ip];
        reporterMap.keySet().forEach(ip -> {
            Reporter<TagData> r = reporterMap.get(ip);
            monitorObserverMap.keySet().stream().filter(id -> {
                String ip2 = id.split(":")[0];
                return ip.equals(ip2);
            }).map(id -> monitorObserverMap.get(id)).map(observer -> {
                observer.resetCallback();
                return observer;
            }).forEach(r::updateObserver);
        });
    }

    // TODO readerを分ける
    @Override
    public void call(TagData tagData) {
        String id = tagData.getIp() + ":" + tagData.getPort();
        if (response != null && this.id.equals(id)) {
            response.onNext(tagData.toTagReport());
        }
    }

    @Override
    public void call(AntennaHealth antennaHealth) {
        if (antennaChangeStreamObserver != null) {
            antennaChangeStreamObserver.onNext(antennaHealth.toAntennaChange());
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

        TagLoggingServer server = new TagLoggingServer(this::call, this::call, this::call);

        ips.parallelStream().forEach(ip -> {
            try {
                // antenna change reporter;
                AntennaChangeListenerImpl antennaChangeListener = new AntennaChangeListenerImpl();
                // tag data reporter;
                TagReportListenerImpl tagReportListener = new TagReportListenerImpl();

                // setting reader
                RFIDReader reader = new RFIDReader(ip);
                reader.connect();
                reader.setting();

                List<AntennaConfig> antennaConfigs = reader.getAcg().getAntennaConfigs();
                List<String> ids = antennaConfigs.stream().map(config -> ip + ":" + config.getPortNumber()).collect(Collectors.toList());

                // set observers for observe tag data
                ids.forEach(id -> {
                    server.addId(id);
                    MonitorObserver monitorObserver = new MonitorObserver();
                    monitorObserverMap.put(id, monitorObserver);
                    tagReportListener.addObserver(monitorObserver);
                });

                RedisObserver redisObserver = new RedisObserver(10000, "redis", 6379);
                tagReportListener.addObserver(redisObserver);
                reporterMap.put(ip, tagReportListener);

                //set observers for observe antenna change
                AntennaHealthObserver antennaHealthObserver = new AntennaHealthObserver(this::call);
                antennaChangeListener.addObserver(antennaHealthObserver);

                reader.setTagReportListener(tagReportListener);
                reader.setAntennaChangeListener(antennaChangeListener);
                reader.start();

            } catch (OctaneSdkException e) {
                e.printStackTrace();
            }
        });

        GrpcServerRunner runner = new GrpcServerRunner(server);
        runner.start();
    }

    public static void main(String[] args) {
        new Main().execute();
    }

}

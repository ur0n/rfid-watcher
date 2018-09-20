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
import com.kron.fluentsample.GetAllReportRequest;
import com.kron.fluentsample.NoParams;
import com.kron.fluentsample.TagReport;
import io.grpc.Attributes;
import io.grpc.stub.StreamObserver;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main implements ITagAllStreamCallback, ITransPortFilterCallback, IAntennaHealthCheckCallback, IAntennaHealthChangeCallback, ICallback {
    private Map<String, ArrayList<Double>> ipAndTxFreq;
    private StreamObserver<TagReport> response;
    private String id;
    private MonitorObserver monitorObserver;

    private Map<String, Reporter<TagData>> reporterMap;
    private StreamObserver<TagReport> tagReportStreamObserver;
    private StreamObserver<AntennaChange> antennaChangeStreamObserver;

    Main() {
        this.ipAndTxFreq = new HashMap<>();
        this.reporterMap = new HashMap<>();
    }

    @Override
    public void call(GetAllReportRequest getAllReportRequest, StreamObserver<TagReport> response) {
        tagReportStreamObserver = response;
    }

    @Override
    public void call(NoParams noParams, StreamObserver<AntennaChange> response) {
        antennaChangeStreamObserver = response;
    }

    // reporterに登録されているmonitorObserverを全て初期化する
    // MonitorObserver.resetCallback()を呼ぶと何もしないコールバックがセットされる
    @Override
    public void call(Attributes attributes) {
        reporterMap.keySet().forEach(ip -> {
            Reporter<TagData> r = reporterMap.get(ip);
            monitorObserver.resetCallback();
            r.updateObserver(monitorObserver);
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

//    private void readYAML() {
//        String root = System.getProperty("user.dir");
//        final Yaml y = new Yaml();
//
//        try (final InputStream is = Files.newInputStream(Paths.get(root + "/config/readers.yml"))) {
//            ips = y.loadAs(is, List.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    // メインの処理
    public void execute() {
        //readYAML();
        ipAndTxFreq.put("192.168.0.102", new ArrayList<>(Collections.singletonList(916.8)));
        ipAndTxFreq.put("192.168.0.103", new ArrayList<>(Collections.singletonList(918.0)));
        ipAndTxFreq.put("192.168.0.104", new ArrayList<>(Collections.singletonList(920.4)));

        // grpcのサーバーを立てる
        TagLoggingServer server = new TagLoggingServer(this::call, this::call, this::call);

        // それぞれのアンテナに対して並列に処理をする
        ipAndTxFreq.entrySet().parallelStream().forEach(entry -> {
            try {
                // antenna change reporter;
                AntennaChangeListenerImpl antennaChangeListener = new AntennaChangeListenerImpl();
                // tag data reporter;
                TagReportListenerImpl tagReportListener = new TagReportListenerImpl();

                // setting reader
                RFIDReader reader = new RFIDReader(entry.getKey());
                reader.connect();
                reader.setting(entry.getValue());

                // アンテナの設定から設定せれてるアンテナのポートナンバーを取得
                List<AntennaConfig> antennaConfigs = reader.getAcg().getAntennaConfigs();
                List<String> ids = antennaConfigs.stream().map(config -> entry.getKey() + ":" + config.getPortNumber()).collect(Collectors.toList());

                // set observers for observe tag data
                ids.forEach(server::addId);

                MonitorObserver monitorObserver = new MonitorObserver();
                tagReportListener.addObserver(monitorObserver);

                RedisObserver redisObserver = new RedisObserver(1000, "redis", 6379);
                tagReportListener.addObserver(redisObserver);
                reporterMap.put(entry.getKey(), tagReportListener);

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

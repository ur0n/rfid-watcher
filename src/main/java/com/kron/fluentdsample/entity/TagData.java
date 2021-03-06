package com.kron.fluentdsample.entity;


import com.kron.fluentsample.TagReport;

import java.util.HashMap;
import java.util.Map;

public class TagData {
    private String ip;
    private int port;
    private String id;
    private double rssi;
    private long time;
    private double phase;
    private double dopplerFrequency;

    public TagData(String ip, int port, String id, double rssi, long time, double phase) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.rssi = rssi;
        this.time = time;
        this.phase = phase;
        this.dopplerFrequency = 0.0;
    }

    public TagData(String ip, int port, String id, double rssi, long time, double phase, double dopplerFrequency) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.rssi = rssi;
        this.time = time;
        this.phase = phase;
        this.dopplerFrequency = dopplerFrequency;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getId() {
        return id;
    }

    public double getRssi() {
        return rssi;
    }

    public long getTime() {
        return time;
    }

    public double getPhase() {
        return phase;
    }


    public TagReport toTagReport() {
        return TagReport.newBuilder()
                .setIp(ip)
                .setPort(port)
                .setId(id)
                .setRssi(rssi)
                .setTime(time)
                .setPhase(phase)
                .setDopplerFrequency(dopplerFrequency)
                .build();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("ip", this.ip);
        data.put("port", this.port);
        data.put("id", this.id);
        data.put("rssi", this.rssi);
        data.put("time", this.time);
        data.put("phase", this.phase);
        data.put("dopplerFrequency", this.dopplerFrequency);
        return data;
    }


    public Map<String, String> toHash() {
        Map<String, String> data = new HashMap<>();
        data.put("ip", this.ip);
        data.put("port", String.valueOf(this.port));
        data.put("id", this.id);
        data.put("rssi", String.valueOf(this.rssi));
        data.put("time", String.valueOf(this.time));
        data.put("phase", String.valueOf(this.phase));
        data.put("dopplerFrequency", String.valueOf(this.dopplerFrequency));
        return data;
    }

    @Override
    public String toString() {
        return "TagData{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", id='" + id + '\'' +
                ", rssi=" + rssi +
                ", time=" + time +
                ", phase=" + phase +
                ", dopplerFrequency=" + dopplerFrequency +
                '}';
    }
}

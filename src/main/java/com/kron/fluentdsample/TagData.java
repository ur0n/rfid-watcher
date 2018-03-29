package com.kron.fluentdsample;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TagData {
    private String ip;
    private int port;
    private String id;
    private double rssi;
    private long time;
    private double phase;

    public TagData(String ip, int port, String id, double rssi, long time, double phase) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.rssi = rssi;
        this.time = time;
        this.phase = phase;
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

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("ip", this.ip);
        data.put("port", this.port);
        data.put("id", this.id);
        data.put("rssi", this.rssi);
        data.put("time", this.time);
        data.put("phase", this.phase);
        return data;
    }

    @Override
    public String toString() {
        return "TagData{" +
                "id=" + id +
                ", rssi=" + rssi +
                ", time=" + time +
                ", phase=" + phase +
                '}';
    }
}

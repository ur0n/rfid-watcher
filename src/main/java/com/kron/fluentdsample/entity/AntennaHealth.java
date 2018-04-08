package com.kron.fluentdsample;

import com.kron.fluentsample.AntennaChange;

import java.util.Objects;

public class AntennaHealth {
    private String ip;
    private int port;
    private int status;

    public AntennaHealth(String ip, int port, int status) {
        this.ip = ip;
        this.port = port;
        this.status = status;
    }

    public AntennaChange toAntennaChange() {
        return AntennaChange.newBuilder()
                .setIp(ip)
                .setPort(port)
                .setStatus(status).build();
    }


    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AntennaHealth that = (AntennaHealth) o;
        return port == that.port &&
                status == that.status &&
                Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ip, port, status);
    }

    @Override
    public String toString() {
        return "AntennaHealth{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", status=" + status +
                '}';
    }
}

package com.kron.fluentdsample.observer.redis;

import com.kron.fluentdsample.entity.TagData;
import com.kron.fluentdsample.observer.Observer;
import com.kron.fluentdsample.reporter.Reporter;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.*;

public class RedisObserver implements Observer<TagData> {
    private long flushInterval;
    private final List<TagData> buffer;
    private Timer t;
    private String host;
    private int port;

    public RedisObserver(long flushInterval, String host, int port) {
        this.flushInterval = flushInterval;
        this.host = host;
        this.port = port;
        this.buffer = new ArrayList<>();
        setUpTimer();
    }

    private void setUpTimer() {
        t = new Timer();
        t.schedule(new FlushTimerTask(host, port), 0, flushInterval);
    }

    public void update(Reporter<TagData> reporter) {
        synchronized (buffer) {
            buffer.add(reporter.getValue());
        }
    }

    private class FlushTimerTask extends TimerTask {
        private Jedis client;

        FlushTimerTask(String host, int port) {
            this.client = new Jedis(host, port);
        }

        @Override
        public void run() {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Random rand = new Random();

            synchronized (buffer) {
                buffer.forEach(data -> {
                    String timeTag = format.format(new Date());
                    String tag = "tag.report" + timeTag + "." + data.getIp() + "." + data.getPort() + "." + data.getId() + "."  + rand.nextInt(100);
                    client.hmset(tag, data.toHash());
                });

//                System.out.println("Flush!");
//                System.out.println("Result: " + buffer.size());

                buffer.clear();
            }
        }
    }
}

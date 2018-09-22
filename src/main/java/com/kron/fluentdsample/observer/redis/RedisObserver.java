package com.kron.fluentdsample.observer.redis;

import com.kron.fluentdsample.entity.TagData;
import com.kron.fluentdsample.observer.Observer;
import com.kron.fluentdsample.reporter.Reporter;
import com.kron.fluentdsample.utility.Notification;
import org.riversun.slacklet.SlackletService;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class RedisObserver implements Observer<TagData> {
    private long flushInterval;
    private final List<TagData> buffer;
    private Timer t;
    private String host;
    private int port;
    private Notification notification;

    public RedisObserver(long flushInterval, String host, int port) {
        this.flushInterval = flushInterval;
        this.host = host;
        this.port = port;
        this.buffer = new ArrayList<>();
        this.notification = new Notification();
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
        private String host;
        private int port;

        FlushTimerTask(String host, int port) {
            this.client = new Jedis(host, port);
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Random rand = new Random();
            notification.start();
            if (!this.client.isConnected()) {
                System.out.println("Redis connection is dead!");
                System.out.println("Reconnecting....");
                client = new Jedis(host, port);
            }

            synchronized (buffer) {
                buffer.forEach(data -> {
                    String timeTag = format.format(new Date());
                    String tag = "tag.report" + timeTag + "." + data.getIp() + "." + data.getPort() + "." + data.getId() + "." + rand.nextInt(100);
                    try {
                    client.hmset(tag, data.toHash());
                    } catch (Exception e) {
                        // エラーのスタックトレースを表示
                        e.printStackTrace();
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        pw.flush();
                        String str = sw.toString();
                        notification.noti2(str);
                    }
                });
                buffer.clear();
            }
            notification.stop();
        }
    }
}

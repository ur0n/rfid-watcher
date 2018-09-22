package com.kron.fluentdsample.utility;

import org.riversun.slacklet.SlackletService;

import java.io.IOException;

public class Notification {
    private String token = "xoxb-439151507652-439634837521-lFzVvw4pHc4YFjcXGGbg4CCn";
    private SlackletService slackletService;
    private String channel = "monitor-notification";

    public Notification() {
        slackletService = new SlackletService(token);
    }

    public void start() {
        try {
            slackletService.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            slackletService.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void noti(String message) {
        slackletService.sendMessageTo(channel, message);
    }

    public void noti2(String message) {
        System.out.println(message);
        slackletService.sendMessageTo(channel, message);
    }
}

package com.kron.fluentdsample;

import com.kron.fluentdsample.observer.monitor.IAntennaHealthCheckCallback;
import com.kron.fluentdsample.observer.monitor.ITagStreamCallback;
import com.kron.fluentdsample.observer.monitor.ITransPortFilterCallback;
import com.kron.fluentdsample.observer.monitor.TagLoggingServer;
import com.kron.fluentsample.AntennaChange;
import com.kron.fluentsample.NoParams;
import com.kron.fluentsample.TagReport;
import io.grpc.Attributes;
import io.grpc.stub.StreamObserver;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class GrpcServer extends Thread implements ITransPortFilterCallback, IAntennaHealthCheckCallback {
    private TagLoggingServer server;

    GrpcServer() {
        this.server = new TagLoggingServer(new TagStreamCallback(), this::call, this::call);
    }

    @Override
    public void call(NoParams noParams, StreamObserver<AntennaChange> response) {

    }

    public class InfinityTask implements Runnable {
        private StreamObserver<TagReport> response;
        private String id;
        private volatile boolean isCanceled;

        public InfinityTask(StreamObserver<TagReport> response, String id) {
            this.response = response;
            this.id = id;
        }

        @Override
        public void run() {
            TagReport reply = TagReport.newBuilder().setId(id).build();
            int i = 0;

            if(Thread.interrupted()) isCanceled = true;

            while (!isCanceled) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    isCanceled = true;
                }

                response.onNext(reply);
                if (i == -1) break;
                i++;
            }
        }
    }


    private class TagStreamCallback implements ITagStreamCallback {
        private ExecutorService executor;
        private Map<String, Future> futureMap;

        TagStreamCallback() {
            this.executor = Executors.newFixedThreadPool(10);
            this.futureMap = new HashMap<>();
        }

        @Override
        public void call(String id, StreamObserver<TagReport> response) {
            System.out.println(futureMap.toString());

            if (futureMap.containsKey(id)) {
                futureMap.get(id).cancel(true);
                futureMap.remove(id);
            } else {
                Future f = executor.submit(new InfinityTask(response, id));
                futureMap.put(id, f);
            }
        }
    }

    @Override
    public void call(Attributes attributes) {
        System.out.println(" ============================ ");
        System.out.println(attributes);
        System.out.println(" ============================ ");
    }

    public void run() {
        try {
            server.start();
            server.blockUntilShutdown();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GrpcServer().start();
    }
}

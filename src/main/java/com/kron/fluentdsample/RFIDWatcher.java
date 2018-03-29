package com.kron.fluentdsample;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class RFIDWatcher {
    private List<String> ips;

    private void execute(){
       readYAML();

       ips.parallelStream().forEach(ip -> {
           Watch w = new Watch(ip);
           w.execute();
       });
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

    public static void main(String[] main){
        RFIDWatcher watcher = new RFIDWatcher();
        watcher.execute();
    }
}

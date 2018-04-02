package com.kron.fluentdsample.reporter;


import com.kron.fluentdsample.TagData;
import com.kron.fluentdsample.reporter.RFIDReader;
import com.kron.fluentdsample.reporter.Reporter;

public class TagDataReporter extends Reporter<TagData> {
    private String ip;
    private RFIDReader reader;
    private TagData tagData;

   public TagDataReporter(String ip) {
        this.ip = ip;
        this.reader = new RFIDReader(ip);
    }

    public TagData getValue() {
        return tagData;
    }

    public void update(TagData tagData){
       this.tagData = tagData;
        notifyObservers();
    }

    @Override
    public void execute() {
        reader.connect();
        reader.settingReader();
        reader.setListeners();
        reader.start();
    }
}


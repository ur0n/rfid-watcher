package com.kron.fluentdsample.reporter;


import com.kron.fluentdsample.entity.TagData;

public class TagDataReporter extends Reporter<TagData> {
    private String ip;
    private RFIDReader reader;
    private TagData tagData;

    public TagDataReporter(RFIDReader reader) {
        this.reader = reader;
    }

    public RFIDReader getReader() {
        return reader;
    }

    public TagData getValue() {
        return tagData;
    }

    public void update(TagData tagData) {
        this.tagData = tagData;
        notifyObservers();
    }

    @Override
    public void execute() {
    }
}


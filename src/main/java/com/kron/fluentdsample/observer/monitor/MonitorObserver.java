package com.kron.fluentdsample.observer.monitor;

import com.kron.fluentdsample.entity.TagData;
import com.kron.fluentdsample.observer.Observer;
import com.kron.fluentdsample.reporter.Reporter;

public class MonitorObserver implements Observer<TagData> {
    private ICallback callback;

    public MonitorObserver() {
        this.callback = new MonitorObserver.NoOperationHandler();
    }

    public void setCallback(ICallback callback) {
        this.callback = callback;
    }

    public void resetCallback(){
        this.callback = new MonitorObserver.NoOperationHandler();
    }

    private static class NoOperationHandler implements ICallback {
        @Override
        public void call(TagData tagData) {
            System.out.println("----------------------");
        }
    }

    @Override
    public void update(Reporter<TagData> reporter) {
        TagData tagData = reporter.getValue();
        callback.call(tagData);
    }
}

package com.kron.fluentdsample.observer.monitor;

import com.kron.fluentdsample.TagData;

public interface ICallback {
    void call(TagData tagData);
}

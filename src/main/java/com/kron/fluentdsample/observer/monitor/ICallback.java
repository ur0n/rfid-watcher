package com.kron.fluentdsample.observer.monitor;

import com.kron.fluentdsample.entity.TagData;

public interface ICallback {
    void call(TagData tagData);
}

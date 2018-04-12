package com.kron.fluentdsample.observer;

import com.kron.fluentdsample.reporter.Reporter;

public interface Observer<T> {
    void update(Reporter<T> reporter);
}

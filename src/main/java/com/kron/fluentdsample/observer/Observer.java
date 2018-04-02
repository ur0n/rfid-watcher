package com.kron.fluentdsample.observer;

import com.kron.fluentdsample.reporter.Reporter;

public interface Observer<T> {
    public abstract void update(Reporter<T> reporter);
}

package com.kron.fluentdsample.server;

import com.kron.fluentdsample.server.ITransPortFilterCallback;
import io.grpc.Attributes;
import io.grpc.ServerTransportFilter;

public class TransPortFilterImpl extends ServerTransportFilter {
    private ITransPortFilterCallback callback;

    public TransPortFilterImpl(ITransPortFilterCallback callback){
        this.callback = callback;
    }

    @Override
    public void transportTerminated(Attributes transportAttrs) {
        System.out.println("terminated" + transportAttrs);
        callback.call(transportAttrs);
    }
}

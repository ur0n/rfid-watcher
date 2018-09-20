package com.kron.fluentdsample.server;

import com.kron.fluentsample.GetAllReportRequest;
import com.kron.fluentsample.NoParams;
import com.kron.fluentsample.TagReport;
import io.grpc.stub.StreamObserver;

public interface ITagAllStreamCallback {
    void call(GetAllReportRequest getAllReportRequest, StreamObserver<TagReport> response);
}

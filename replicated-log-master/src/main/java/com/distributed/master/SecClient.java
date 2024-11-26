package com.distributed.master;

import com.distributed.commons.LogItem;
import com.distributed.stubs.*;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class SecClient {

    private static final Logger log = LoggerFactory.getLogger(SecClient.class);

    private final String host;
    private final ManagedChannel channel;
    private final LogAppendServiceGrpc.LogAppendServiceStub asyncStub;
    private final HealthServiceGrpc.HealthServiceBlockingStub healthService;

    public SecClient(final String host, final int port) {
        this.host = host;
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.asyncStub = LogAppendServiceGrpc.newStub(this.channel);
        this.healthService = HealthServiceGrpc.newBlockingStub(this.channel);
    }

    private String getHost() {
        return host;
    }

    public void asyncReplicateLog(final LogItem item, final CountDownLatch writeConcernLatch) {
        LogRequest request = LogRequest.newBuilder()
                .setId(item.getId())
                .setMessage(item.getMessage())
                .build();

        StreamObserver<LogResponse> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(LogResponse logResponse) {
                log.info("Response from {} node: {}", getHost(), logResponse.getResponseMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                // TODO retry logic should be implemented here in v3
                log.error("Replication of {} to {} Failed: {}", item.getMessage(), getHost(), Status.fromThrowable(throwable));
                throw new RuntimeException(String.format("Replication of %s to %s Failed due to status: %s",
                        item.getMessage(), getHost(), Status.fromThrowable(throwable)),
                        throwable);
            }

            @Override
            public void onCompleted() {
                log.info("Replication of {} to {} Completed", item.getMessage(), getHost());
                writeConcernLatch.countDown();
            }
        };

        asyncStub.append(request, responseObserver);
    }

    public void syncPing() {
        HealthResponse health = this.healthService.health(Empty.getDefaultInstance());
    }


}

package com.distributed.master;

import com.distributed.commons.LogItem;
import com.distributed.stubs.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SecClient {

    private static final Logger log = LoggerFactory.getLogger(SecClient.class);

    private final String host;
    private final String name;
    private final ManagedChannel channel;
    private final LogAppendServiceGrpc.LogAppendServiceStub asyncStub;
    private final HealthServiceGrpc.HealthServiceBlockingStub healthService;

    public SecClient(final String host, final int port, final String name) {
        this.host = host;
        this.name = name;
        final Map<String, ?> serviceConfig = getRetryingServiceConfig();
        log.info("Client started with retrying configuration: " + serviceConfig);
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext()
//                .disableServiceConfigLookUp().defaultServiceConfig(serviceConfig).enableRetry()
                .build();
        this.asyncStub = LogAppendServiceGrpc.newStub(this.channel);
        this.healthService = HealthServiceGrpc.newBlockingStub(this.channel);
    }

    private static Map<String, ?> getRetryingServiceConfig() {
        return new Gson()
                .fromJson(
                        new JsonReader(
                                new InputStreamReader(
                                        SecClient.class.getClassLoader().getResourceAsStream("retrying_service_config.json"), UTF_8)),
                        Map.class);
    }

    private String getHost() {
        return host;
    }

    private String getName() {
        return name;
    }

    public void asyncReplicateLog(final List<LogItem> logItems, final CountDownLatch writeConcernLatch) {

        List<LogRequest.LogEntry> logEntries = logItems.stream().map(item -> LogRequest.LogEntry.newBuilder().setId(item.getId()).setMessage(item.getMessage()).build()).toList();

        LogRequest.Builder builder = LogRequest.newBuilder();
        logEntries.forEach(builder::addLogEntries);
        LogRequest request = builder.build();

        StreamObserver<LogResponse> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(LogResponse logResponse) {
                log.info("Response from {}-{} node: {}",getName(), getHost(), logResponse.getResponseMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                // TODO retry logic should be implemented here in v3
                log.error("Replication of LogEntry to {}-{} Failed: {}",getName(), getHost(), Status.fromThrowable(throwable));
                log.error("Service temporarily unavailable would go for retry if the policy permits");
//                throw new RuntimeException(String.format("Replication of LogEntry to %s Failed due to status: %s",
//                        getHost(), Status.fromThrowable(throwable)),
//                        throwable);
            }

            @Override
            public void onCompleted() {
                log.info("Replication of items to {}-{} Completed", getName(), getHost());
                writeConcernLatch.countDown();
            }
        };


        asyncStub.appendEntries(request, responseObserver);
    }

    public long syncPing(int deadlineTimeSeconds) {
        HealthResponse health = this.healthService
                .withDeadlineAfter(deadlineTimeSeconds, TimeUnit.SECONDS)
                .health(Empty.getDefaultInstance());
        return health.getLastId();
    }
}

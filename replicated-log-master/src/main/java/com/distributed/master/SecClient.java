package com.distributed.master;

import com.distributed.commons.LogItem;
import com.distributed.stubs.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.protobuf.Empty;
import io.grpc.*;
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
    private final ManagedChannel originalChannel;
    private final LogAppendServiceGrpc.LogAppendServiceStub asyncStub;
    private final HealthServiceGrpc.HealthServiceBlockingStub healthService;

    public SecClient(final String host, final int port, final String name) {
        this.host = host;
        this.name = name;
        final Map<String, ?> serviceConfig = getRetryingServiceConfig();
        log.info("Client started with retrying configuration: " + serviceConfig);
        this.originalChannel = ManagedChannelBuilder.forAddress(host, port).usePlaintext()
                .disableServiceConfigLookUp().defaultServiceConfig(serviceConfig).enableRetry()
                .build();
        Channel channel = ClientInterceptors.intercept(this.originalChannel, new RetryLoggingInterceptor());
        this.asyncStub = LogAppendServiceGrpc.newStub(channel);
        this.healthService = HealthServiceGrpc.newBlockingStub(channel);
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

    public void asyncReplicateLog(final List<LogItem> logItems, final CountDownLatch writeConcernLatch, boolean waitForReady) {

        List<LogRequest.LogEntry> logEntries = logItems.stream().map(item -> LogRequest.LogEntry.newBuilder().setId(item.getId()).setMessage(item.getMessage()).build()).toList();

        LogRequest.Builder builder = LogRequest.newBuilder();
        logEntries.forEach(builder::addLogEntries);
        LogRequest request = builder.build();

        StreamObserver<LogResponse> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(LogResponse logResponse) {
                log.info("Response from {}: {} node: {}",getName(), getHost(), logResponse.getResponseMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Replication of LogEntry to {}: {} Failed: {}",getName(), getHost(), Status.fromThrowable(throwable));
                log.error("Service temporarily unavailable would go for retry if the policy permits");
            }

            @Override
            public void onCompleted() {
                log.info("Replication of items to {}: {} Completed", getName(), getHost());
                if(writeConcernLatch != null) {
                    writeConcernLatch.countDown();
                }
            }
        };

        if (waitForReady) {
            asyncStub.withWaitForReady().appendEntries(request, responseObserver);
        } else {
            asyncStub.appendEntries(request, responseObserver);
        }


    }

    public long syncPing(int deadlineTimeSeconds) {
        HealthResponse health = this.healthService
                .withDeadlineAfter(deadlineTimeSeconds, TimeUnit.SECONDS)
                .health(Empty.getDefaultInstance());
        return health.getLastId();
    }
}

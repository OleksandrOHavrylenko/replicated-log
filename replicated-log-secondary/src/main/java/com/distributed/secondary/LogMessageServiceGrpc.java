package com.distributed.secondary;

import com.distributed.commons.LogItem;
import com.distributed.stubs.LogAppendServiceGrpc;
import com.distributed.stubs.LogRequest;
import com.distributed.stubs.LogResponse;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

//@GrpcService
@Service
public class LogMessageServiceGrpc extends LogAppendServiceGrpc.LogAppendServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(LogMessageServiceGrpc.class);

    private final LogRepository logRepository;

    final int sleepInMillis;

    public LogMessageServiceGrpc(final LogRepository logRepository, @Value("${grpc.server.sleepInMillis}") final int sleepInMillis) {
        this.sleepInMillis = sleepInMillis;
        this.logRepository = Objects.requireNonNull(logRepository);
    }

    @Override
    public void append(LogRequest request, StreamObserver<LogResponse> responseObserver) {
        sleep();
        logRepository.add(new LogItem(request.getId(), request.getMessage()));
        LogResponse response = LogResponse
                .newBuilder()
                .setResponseMessage("OK Sec " + request.getMessage())
                .build();

        // Send the response to the client.
        responseObserver.onNext(response);

        // Notifies the customer that the call is completed.
        responseObserver.onCompleted();
    }

    private void sleep() {
        try {
            Thread.sleep(sleepInMillis);
        } catch (InterruptedException e) {
            log.error("InterruptedException occurred while sleep", e);
        }
    }
}

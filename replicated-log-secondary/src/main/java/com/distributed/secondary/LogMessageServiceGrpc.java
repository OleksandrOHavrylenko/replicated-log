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

import java.util.List;
import java.util.Objects;

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
    public void appendEntries(LogRequest request, StreamObserver<LogResponse> responseObserver) {
        sleep();

        List<LogItem> logItems = request.getLogEntriesList().stream().map(logEntry -> new LogItem(logEntry.getId(), logEntry.getMessage())).toList();

        for (LogItem logItem : logItems) {

            if (IdChecker.getNext() == logItem.getId()) {
                logRepository.add(logItem);
                IdChecker.incrementAndGet();
                log.warn("Correct LogItem {} saved to logRepository.", logItem);
            } else if (IdChecker.getNext() > logItem.getId()) {
                log.warn("Old LogItem {} tries to be saved and is ignored.", logItem);
            } else {
                log.warn("LogItem {} saved to the buffer and waits for its order.", logItem);
            }
        }

        LogResponse response = LogResponse
                .newBuilder()
                .setNextId(IdChecker.getNext())
                .setResponseMessage(String.format("ACK %d", IdChecker.getLast()))
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

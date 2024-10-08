package com.distributed.secondary;

import com.distributed.stubs.LogAppendServiceGrpc;
import com.distributed.stubs.LogMessage;
import com.distributed.stubs.LogResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@GrpcService
public class LogMessageControllerGrpc extends LogAppendServiceGrpc.LogAppendServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(LogMessageControllerGrpc.class);

    private final LogRepository logRepository;

    public LogMessageControllerGrpc(final LogRepository logRepository) {
        this.logRepository = Objects.requireNonNull(logRepository);
    }


    private LogResponse append(LogMessage logMessage) {
        logRepository.add(new Message(logMessage));
        return LogResponse
                .newBuilder()
                .setResponseMessage("OK Sec " + logMessage.getMessage())
                .build();
    }

    @Override
    public StreamObserver<LogMessage> append(StreamObserver<LogResponse> responseObserver
    ) {
        return StreamObserverUtility.proxyStream(
                responseObserver,
                this::append
        );
    }
}

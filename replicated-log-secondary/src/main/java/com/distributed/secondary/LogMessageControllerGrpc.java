package com.distributed.secondary;

import com.distributed.stubs.LogAppendServiceGrpc;
import com.distributed.stubs.LogMessage;
import com.distributed.stubs.LogResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class LogMessageControllerGrpc extends LogAppendServiceGrpc.LogAppendServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(LogMessageControllerGrpc.class);

    private LogResponse append(LogMessage logMessage) {
        log.info("Received request: {}", logMessage);
        System.out.println("Received request: " + logMessage);

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

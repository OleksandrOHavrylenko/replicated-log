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

    @Override
    public void append(LogMessage request, StreamObserver<LogResponse> responseObserver) {
        logRepository.add(new Message(request.getId(), request.getMessage()));
        LogResponse response = LogResponse
                .newBuilder()
                .setResponseMessage("OK Sec " + request.getMessage())
                .build();

        // Send the response to the client.
        responseObserver.onNext(response);

        // Notifies the customer that the call is completed.
        responseObserver.onCompleted();
    }
}

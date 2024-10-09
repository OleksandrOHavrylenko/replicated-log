package com.distributed.secondary;

import com.distributed.stubs.LogAppendServiceGrpc;
import com.distributed.stubs.LogMessage;
import com.distributed.stubs.LogResponse;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

//@GrpcService
@Service
public class LogMessageServiceGrpc extends LogAppendServiceGrpc.LogAppendServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(LogMessageServiceGrpc.class);

    private final LogRepository logRepository;

    public LogMessageServiceGrpc(final LogRepository logRepository) {
        this.logRepository = Objects.requireNonNull(logRepository);
    }

    @Override
    public void append(LogMessage request, StreamObserver<LogResponse> responseObserver) {
        logRepository.add(new Message(request.getId(), request.getMessage()));
        LogResponse response = LogResponse
                .newBuilder()
                .setResponseMessage("OK Sec " + request.getMessage())
                .build();

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            log.error("InterruptedException occurred while sleep", e);
//        }
        // Send the response to the client.
        responseObserver.onNext(response);

        // Notifies the customer that the call is completed.
        responseObserver.onCompleted();
    }
}

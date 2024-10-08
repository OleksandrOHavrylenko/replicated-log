package com.distributed.master;

import com.distributed.stubs.LogAppendServiceGrpc;
import com.distributed.stubs.LogMessage;
import com.distributed.stubs.LogResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SecondaryClient {

    private static final Logger log = LoggerFactory.getLogger(SecondaryClient.class);

    @GrpcClient("sec")
    LogAppendServiceGrpc.LogAppendServiceStub stub;

    public void appendLog(Message message) {

        final StreamObserver<LogMessage> request = stub.append(getResponseObserver());
        LogMessage logMessage = LogMessage.newBuilder().setId(message.getId()).setMessage(message.getMessage()).build();
        request.onNext(logMessage);
    }

    private static StreamObserver<LogResponse> getResponseObserver() {
        return new StreamObserver<>() {
            @Override
            public void onNext(LogResponse logResponse) {
                System.out.println("LogResponse from sec " + logResponse.getResponseMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Error occured", throwable);
            }

            @Override
            public void onCompleted() {
                log.info("Log request finished");
            }
        };
    }
}

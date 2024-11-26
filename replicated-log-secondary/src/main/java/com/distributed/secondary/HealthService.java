package com.distributed.secondary;

import com.distributed.stubs.HealthResponse;
import com.distributed.stubs.HealthServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HealthService extends HealthServiceGrpc.HealthServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(HealthService.class);

    @Override
    public void health(Empty request, StreamObserver<HealthResponse> responseObserver) {
        HealthResponse response = HealthResponse
                .newBuilder()
                .setLastId(IdChecker.getLast())
                .build();

        // Send the response to the client.
        responseObserver.onNext(response);

        // Notifies the customer that the call is completed.
        responseObserver.onCompleted();
    }
}

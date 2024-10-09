package com.distributed.master;

import com.distributed.stubs.LogAppendServiceGrpc;
import com.distributed.stubs.LogRequest;
import com.distributed.stubs.LogResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecClient {

    private static final Logger log = LoggerFactory.getLogger(SecClient.class);

    private final ManagedChannel channel;
    private LogAppendServiceGrpc.LogAppendServiceBlockingStub client;

    public SecClient(final String address, final int port) {
        channel = ManagedChannelBuilder.forAddress(address, port).usePlaintext().build();
        client = LogAppendServiceGrpc.newBlockingStub(channel);
    }

    public String replicateLog(final Message message) {

        LogRequest request = LogRequest.newBuilder()
                .setId(message.getId())
                .setMessage(message.getMessage())
                .build();

        LogResponse response = client.append(request);
        log.info("Response from secondary node: {}", response.getResponseMessage());

        return response.getResponseMessage();
    }
}

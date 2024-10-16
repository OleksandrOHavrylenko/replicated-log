package com.distributed.master;

import com.distributed.commons.LogItem;
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

    private final String host;

    public SecClient(final String host, final int port) {
        this.host = host;
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        client = LogAppendServiceGrpc.newBlockingStub(channel);
    }

    public String getHost() {
        return host;
    }

    public String replicateLog(final LogItem item) {

        LogRequest request = LogRequest.newBuilder()
                .setId(item.getId())
                .setMessage(item.getMessage())
                .build();

        LogResponse response = client.append(request);
        log.info("Response from secondary node: {}", response.getResponseMessage());

        return response.getResponseMessage();
    }
}

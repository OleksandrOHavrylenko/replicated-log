package com.distributed.master;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryLoggingInterceptor implements ClientInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RetryLoggingInterceptor.class);

    @VisibleForTesting
    static final Metadata.Key<String> RPC_ATTEMPTS_KEY =
            Metadata.Key.of("grpc-previous-rpc-attempts", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
                                                               CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        String retries = headers.get(RPC_ATTEMPTS_KEY);
                        if (retries != null) {
                            log.info("Retries to copy:" + retries);
                        }
                        super.onHeaders(headers);
                    }
                }, headers);
            }
        };
    }
}

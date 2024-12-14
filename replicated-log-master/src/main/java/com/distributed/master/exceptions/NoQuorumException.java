package com.distributed.master.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoQuorumException extends ResponseStatusException {
    private final long availableNodes;
    private final long totalNodes;

    public NoQuorumException(long availableNodes, long totalNodes) {
        super(HttpStatus.METHOD_NOT_ALLOWED, String.format("ReadOnly mode, no Quorum available, only %d nodes available out of %d nodes", availableNodes, totalNodes));
        this.availableNodes = availableNodes;
        this.totalNodes = totalNodes;
    }

    public long getAvailableNodes() {
        return availableNodes;
    }

    public long getTotalNodes() {
        return totalNodes;
    }
}

package com.distributed.master.replica;

import com.distributed.master.RestoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ReplicaRepository {
    private final List<Replica> replicas;

    public ReplicaRepository(@Value("${client.sec1.host}") final String sec1Host, @Value("${client.sec1.port}") final int sec1Port,
                             @Value("${client.sec2.host}") final String sec2Host, @Value("${client.sec2.port}") final int sec2Port,
                             final RestoreService restoreService) {
        this.replicas = Arrays.asList(new Replica(sec1Host, sec1Port, "secondary1", restoreService), new Replica(sec2Host, sec2Port, "secondary2", restoreService));
    }

    public List<Replica> getReplicas() {
        return replicas;
    }

    public int getReplicasCount() {
        return this.replicas.size();
    }
}

package com.distributed.master.replica;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ReplicaRepository {
    private final List<Replica> replicas;

    public ReplicaRepository(@Value("${client.sec1.host}") final String sec1Host, @Value("${client.sec1.port}") final int sec1Port,
                             @Value("${client.sec2.host}") final String sec2Host, @Value("${client.sec2.port}") final int sec2Port) {
        this.replicas = Arrays.asList(new Replica(sec1Host, sec1Port, "secondary1"), new Replica(sec2Host, sec2Port, "secondary2"));
    }

    public List<Replica> getReplicas() {
        return replicas;
    }

    public int getReplicasCount() {
        return this.replicas.size();
    }
}

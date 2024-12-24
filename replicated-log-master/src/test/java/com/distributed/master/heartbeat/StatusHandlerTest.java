package com.distributed.master.heartbeat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.distributed.master.heartbeat.ReplicaStatus.*;
import static org.junit.jupiter.api.Assertions.*;

class StatusHandlerTest {
    private StatusHandler statusHandler;

    @BeforeEach
    void setUp() {
        statusHandler = new StatusHandler();
    }

    @Test
    void testDefaultStatus() {
        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(UNHEALTHY, actual);
    }

    @Test
    void testUnhealthyDownStatus() {
        statusHandler.statusDown();

        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(UNHEALTHY, actual);
    }

    @Test
    void test1UpStatus() {
        statusHandler.statusUp();

        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(SUSPECTED, actual);
    }

    @Test
    void test1Up1DownStatus() {
        statusHandler.statusUp();
        statusHandler.statusDown();

        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(SUSPECTED, actual);
    }

    @Test
    void test1Up2DownStatus() {
        statusHandler.statusUp();
        statusHandler.statusDown();
        statusHandler.statusDown();

        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(SUSPECTED, actual);
    }

    @Test
    void test1Up3DownStatus() {
        statusHandler.statusUp();
        statusHandler.statusDown();
        statusHandler.statusDown();
        statusHandler.statusDown();

        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(UNHEALTHY, actual);
    }

    @Test
    void test2UpStatus() {
        statusHandler.statusUp();
        statusHandler.statusUp();

        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(HEALTHY, actual);
    }

    @Test
    void test2Up1DownStatus() {
        statusHandler.statusUp();
        statusHandler.statusUp();
        statusHandler.statusDown();

        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(HEALTHY, actual);
    }
    @Test
    void test2Up2DownStatus() {
        statusHandler.statusUp();
        statusHandler.statusUp();
        statusHandler.statusDown();
        statusHandler.statusDown();

        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(HEALTHY, actual);
    }

    @Test
    void test2Up3ownStatus() {
        statusHandler.statusUp();
        statusHandler.statusUp();
        statusHandler.statusDown();
        statusHandler.statusDown();
        statusHandler.statusDown();

        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(SUSPECTED, actual);
    }

    @Test
    void test3UpStatus() {
        statusHandler.statusUp();
        statusHandler.statusUp();
        statusHandler.statusUp();

        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(HEALTHY, actual);
    }

    @Test
    void test3Up1DownStatus() {
        statusHandler.statusUp();
        statusHandler.statusUp();
        statusHandler.statusUp();
        statusHandler.statusDown();

        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(HEALTHY, actual);
    }

    @Test
    void test3Up2DownStatus() {
        statusHandler.statusUp();
        statusHandler.statusUp();
        statusHandler.statusUp();
        statusHandler.statusDown();
        statusHandler.statusDown();

        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(HEALTHY, actual);
    }

    @Test
    void test3Up3DownStatus() {
        statusHandler.statusUp();
        statusHandler.statusUp();
        statusHandler.statusUp();
        statusHandler.statusDown();
        statusHandler.statusDown();
        statusHandler.statusDown();

        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(SUSPECTED, actual);
    }

    @Test
    void test3Up3Down1UpStatus() {
        statusHandler.statusUp();
        statusHandler.statusUp();
        statusHandler.statusUp();
        statusHandler.statusDown();
        statusHandler.statusDown();
        statusHandler.statusDown();
        statusHandler.statusUp();

        ReplicaStatus actual = statusHandler.getStatus();

        assertNotNull(actual);
        assertEquals(HEALTHY, actual);
    }
}
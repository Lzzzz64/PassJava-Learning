
package org.example.passjava.study.integration;

import org.example.passjava.study.support.StudyTestcontainers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContainerSmokeTest extends StudyTestcontainers {

    @Test
    void shouldStartAllContainers() {

        assertTrue(MYSQL.isRunning());
        assertTrue(REDIS.isRunning());
        assertTrue(RABBITMQ.isRunning());

        assertNotNull(MYSQL.getJdbcUrl());
        assertNotNull(REDIS.getFirstMappedPort());
        assertNotNull(RABBITMQ.getAmqpPort());
    }
}

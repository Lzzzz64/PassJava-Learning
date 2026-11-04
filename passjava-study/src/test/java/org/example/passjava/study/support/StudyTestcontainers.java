
package org.example.passjava.study.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class StudyTestcontainers {

    @Container
    public static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("passjava_study_test")
                    .withUsername("test")
                    .withPassword("test123");

    @Container
    public static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:7.4-alpine")
                    .withExposedPorts(6379);

    @Container
    public static final RabbitMQContainer RABBITMQ =
            new RabbitMQContainer("rabbitmq:4.1-management");

    @DynamicPropertySource
    static void registerProperties(
            DynamicPropertyRegistry registry) {

        // MySQL
        registry.add(
                "spring.datasource.url",
                MYSQL::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                MYSQL::getUsername
        );

        registry.add(
                "spring.datasource.password",
                MYSQL::getPassword
        );

        // Redis
        registry.add(
                "spring.data.redis.host",
                REDIS::getHost
        );

        registry.add(
                "spring.data.redis.port",
                REDIS::getFirstMappedPort
        );

        // RabbitMQ
        registry.add(
                "spring.rabbitmq.host",
                RABBITMQ::getHost
        );

        registry.add(
                "spring.rabbitmq.port",
                RABBITMQ::getAmqpPort
        );

        registry.add(
                "spring.rabbitmq.username",
                RABBITMQ::getAdminUsername
        );

        registry.add(
                "spring.rabbitmq.password",
                RABBITMQ::getAdminPassword
        );
    }
}

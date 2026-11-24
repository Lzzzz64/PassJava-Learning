
package org.example.passjava.study.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;

public abstract class StudyTestcontainers {

    // MySQL
    public static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("passjava_study_test")
                    .withUsername("test")
                    .withPassword("test123")
                    .withInitScript("sql/04_study.sql");

    // Redis
    public static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:7.4-alpine")
                    .withExposedPorts(6379);

    // RabbitMQ
    public static final RabbitMQContainer RABBITMQ =
            new RabbitMQContainer("rabbitmq:4.1-management");

    /*
     * 类加载时启动容器。
     * 整个测试 JVM 期间保持运行，不再按测试类停止和重启。
     */
    static {
        MYSQL.start();
        REDIS.start();
        RABBITMQ.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {

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

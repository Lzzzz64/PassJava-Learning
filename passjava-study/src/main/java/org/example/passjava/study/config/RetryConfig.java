package org.example.passjava.study.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {

    @Bean
    public RetryTemplate studyRetryTemplate() {

        return RetryTemplate.builder()
                .maxAttempts(3)
                .exponentialBackoff(500, 2, 2000)
                .retryOn(TransientDataAccessException.class)
                .build();
    }
}

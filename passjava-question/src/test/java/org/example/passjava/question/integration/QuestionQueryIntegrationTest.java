package org.example.passjava.question.integration;

import org.example.passjava.question.entity.QmsQuestion;
import org.example.passjava.question.mapper.QmsQuestionMapper;
import org.example.passjava.question.service.impl.QmsQuestionServiceImpl;
import org.example.passjava.question.support.QuestionTestcontainers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "spring.cloud.nacos.discovery.enabled=false",
        "spring.cloud.nacos.config.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.service-registry.auto-registration.enabled=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class QuestionQueryIntegrationTest extends QuestionTestcontainers {

    @Autowired
    private QmsQuestionServiceImpl qmsQuestionService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // Spy：执行真实 Mapper 方法，同时记录调用次数
    @MockitoSpyBean
    private QmsQuestionMapper questionMapper;

    @Test
    void concurrentCacheMissShouldQueryDatabaseOnce()
            throws Exception {

        // 使用测试数据库中已经存在的题目
        Long questionId = 1L;

        String detailKey =
                "passjava:question:detail:" + questionId;

        String versionKey =
                "passjava:question:version:" + questionId;

        // 删除测试 Redis 中的缓存，确保初始状态为缓存未命中
        redisTemplate.delete(List.of(detailKey, versionKey));

        // 清除 Spy 之前记录的调用次数
        clearInvocations(questionMapper);

        int tasks = 100;

        ExecutorService executor =
                Executors.newFixedThreadPool(20);

        CountDownLatch start = new CountDownLatch(1);

        List<Future<QmsQuestion>> futures =
                new ArrayList<>();

        try {

            // 创建100个并发查询任务
            for (int i = 0; i < tasks; i++) {

                futures.add(executor.submit(() -> {

                    // 等待主线程统一放行
                    start.await();

                    return qmsQuestionService
                            .getQuestionById(questionId);
                }));
            }

            // 同时放行当前等待的线程
            start.countDown();

            // 检查100个请求的查询结果
            for (Future<QmsQuestion> future : futures) {

                QmsQuestion result =
                        future.get(10, TimeUnit.SECONDS);

                assertNotNull(result);

                assertEquals(
                        "什么是Java多态？",
                        result.getTitle()
                );
            }

            // 核心断言：数据库只被查询一次
            verify(questionMapper, times(1))
                    .selectById(questionId);

            // 验证查询后 Redis 中确实存在详情缓存
            assertNotNull(
                    redisTemplate.opsForValue().get(detailKey)
            );

        } finally {

            start.countDown();

            executor.shutdownNow();

            executor.awaitTermination(
                    5, TimeUnit.SECONDS
            );

            // 只清理测试使用的 Redis Key
            redisTemplate.delete(
                    List.of(detailKey, versionKey)
            );
        }
    }
}
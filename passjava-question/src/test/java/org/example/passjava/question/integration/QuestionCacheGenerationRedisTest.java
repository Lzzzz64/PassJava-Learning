package org.example.passjava.question.integration;

import org.example.passjava.question.service.QmsQuestionCacheService;
import org.example.passjava.question.support.QuestionTestcontainers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.cloud.nacos.discovery.enabled=false",
        "spring.cloud.nacos.config.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.service-registry.auto-registration.enabled=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class QuestionCacheGenerationRedisTest extends QuestionTestcontainers {

    private static final String DETAIL_PREFIX =
            "passjava:question:detail:";

    private static final String VERSION_PREFIX =
            "passjava:question:version:";

    @Autowired
    private QmsQuestionCacheService questionCacheService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 测试1：
     * 旧查询线程不能将过期数据重新写入缓存
     */
    @Test
    void staleLoaderMustNotRepopulateCache() {

        Long id = newTestQuestionId();

        String detailKey = DETAIL_PREFIX + id;
        String versionKey = VERSION_PREFIX + id;

        String oldJson =
                "{\"id\":" + id + ",\"title\":\"旧标题\"}";

        String newJson =
                "{\"id\":" + id + ",\"title\":\"新标题\"}";

        try {

            // A：读取旧世代号
            String oldGeneration =
                    questionCacheService.getGeneration(id);

            assertEquals("0", oldGeneration);

            // B：模拟数据库更新完成，执行缓存失效
            questionCacheService.evictAndIncrementVersion(id);

            assertEquals(
                    "1",
                    questionCacheService.getGeneration(id)
            );

            // A：试图写入旧数据，必须失败
            boolean oldSaved =
                    questionCacheService.saveIfUnchanged(
                            id,
                            oldGeneration,
                            oldJson
                    );

            assertFalse(oldSaved);

            assertNull(
                    redisTemplate.opsForValue().get(detailKey)
            );

            // 新查询使用最新世代号写入数据
            String newGeneration =
                    questionCacheService.getGeneration(id);

            boolean newSaved =
                    questionCacheService.saveIfUnchanged(
                            id,
                            newGeneration,
                            newJson
                    );

            assertTrue(newSaved);

            assertEquals(
                    newJson,
                    redisTemplate.opsForValue().get(detailKey)
            );

        } finally {

            redisTemplate.delete(
                    List.of(detailKey, versionKey)
            );
        }
    }

    /**
     * 测试2：
     * 缓存失效必须删除已有缓存
     */
    @Test
    void invalidationMustRemoveExistingDetailCache() {

        Long id = newTestQuestionId();

        String detailKey = DETAIL_PREFIX + id;
        String versionKey = VERSION_PREFIX + id;

        String json =
                "{\"id\":" + id + ",\"title\":\"测试题目\"}";

        try {

            String generation =
                    questionCacheService.getGeneration(id);

            assertEquals("0", generation);

            // 写入原始缓存
            assertTrue(
                    questionCacheService.saveIfUnchanged(
                            id,
                            generation,
                            json
                    )
            );

            assertEquals(
                    json,
                    redisTemplate.opsForValue().get(detailKey)
            );

            // 模拟题目修改后的缓存失效
            questionCacheService.evictAndIncrementVersion(id);

            // 世代号增加
            assertEquals(
                    "1",
                    questionCacheService.getGeneration(id)
            );

            // 旧缓存删除
            assertNull(
                    redisTemplate.opsForValue().get(detailKey)
            );

        } finally {

            redisTemplate.delete(
                    List.of(detailKey, versionKey)
            );
        }
    }

    /**
     * 测试3：
     * 旧查询线程不能缓存过期的空值标记
     */
    @Test
    void staleNullLoaderMustNotCacheNotFound() {

        Long id = newTestQuestionId();

        String detailKey = DETAIL_PREFIX + id;
        String versionKey = VERSION_PREFIX + id;

        try {

            // A：查询不到题目，记录当前世代号
            String oldGeneration =
                    questionCacheService.getGeneration(id);

            assertEquals("0", oldGeneration);

            // B：模拟题目被创建，世代号发生变化
            questionCacheService.evictAndIncrementVersion(id);

            // A：旧查询线程不能写入空值标记
            assertFalse(
                    questionCacheService.saveNullIfUnchanged(
                            id,
                            oldGeneration
                    )
            );

            // Redis 中不应该存在旧空值
            assertNull(
                    redisTemplate.opsForValue().get(detailKey)
            );

        } finally {

            redisTemplate.delete(
                    List.of(detailKey, versionKey)
            );
        }
    }

    /**
     * 生成测试专用题目 ID，避免和真实数据冲突
     */
    private static Long newTestQuestionId() {

        return ThreadLocalRandom.current().nextLong(
                4_000_000_000_000_000_000L,
                8_000_000_000_000_000_000L
        );
    }
}
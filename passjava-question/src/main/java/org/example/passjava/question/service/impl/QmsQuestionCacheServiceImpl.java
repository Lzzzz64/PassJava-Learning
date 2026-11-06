package org.example.passjava.question.service.impl;

import org.example.passjava.question.service.QmsQuestionCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class QmsQuestionCacheServiceImpl implements QmsQuestionCacheService {
    private static final Logger log =
            LoggerFactory.getLogger(QmsQuestionCacheServiceImpl.class);

    private static final String PREFIX =
            "passjava:question:detail:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final DefaultRedisScript<Long> SAVE_IF_CURRENT;

    static {
        SAVE_IF_CURRENT = new DefaultRedisScript<>();

        SAVE_IF_CURRENT.setScriptText("""
        local version = redis.call('GET', KEYS[1]) or '0'

        if version ~= ARGV[1] then
            return 0
        end

        redis.call(
            'SET', KEYS[2], ARGV[2],
            'EX', tonumber(ARGV[3])
        )

        return 1
        """);

        SAVE_IF_CURRENT.setResultType(Long.class);
    }

    private static final DefaultRedisScript<Long> INVALIDATE;

    static {
        INVALIDATE = new DefaultRedisScript<>();

        INVALIDATE.setScriptText("""
        local generation = redis.call('INCR', KEYS[1])
        redis.call('DEL', KEYS[2])
        return generation
        """);

        INVALIDATE.setResultType(Long.class);
    }

    // 世代号key
    public String versionKey(Long id) {
        return "passjava:question:version:" + id;
    }

    // 获取世代号
    @Override
    public String getGeneration(Long id) {
        String value = redisTemplate.opsForValue()
                .get(versionKey(id));

        return value == null ? "0" : value;
    }

    // 世代号未变化才保存缓存
    @Override
    public boolean saveIfUnchanged(Long id, String expectedGeneration, String json) {
        try{
            long ttl = 600 +
                    ThreadLocalRandom.current().nextLong(61);

            Long result = redisTemplate.execute(
                    SAVE_IF_CURRENT,
                    List.of(versionKey(id), PREFIX + id),
                    expectedGeneration,
                    json,
                    String.valueOf(ttl)
            );
            return Long.valueOf(1).equals(result);
        } catch (DataAccessException e){
            log.error("Redis保存失败，题目ID={}", id, e);
            return false;
        }
    }

    @Override
    public void save(Long id, String json) {
        // 10分钟 + 0~60秒随机过期时间
        long seconds = 600 +
                ThreadLocalRandom.current().nextLong(61);
        try{
            redisTemplate.opsForValue().set(PREFIX + id, json,
                    Duration.ofSeconds(seconds));
        } catch (DataAccessException e){
            log.error("Redis保存失败，题目ID={}", id, e);
        }
    }

    @Override
    public boolean saveNullIfUnchanged(Long id, String expectedGeneration) {
        try{
            Long result = redisTemplate.execute(
                    SAVE_IF_CURRENT,
                    List.of(versionKey(id), PREFIX + id),
                    expectedGeneration,
                    NULL_VALUE,
                    "120"
            );
            return Long.valueOf(1).equals(result);
        } catch (DataAccessException e){
            log.error("Redis保存失败，题目ID={}", id, e);
            return false;
        }
    }

    @Override
    public void saveNull(Long id) {
        // 10分钟 + 0~60秒随机过期时间
        long seconds = 600 +
                ThreadLocalRandom.current().nextLong(61);
        try{
            redisTemplate.opsForValue().set(PREFIX + id, NULL_VALUE,
                    Duration.ofMinutes(2));
        } catch (DataAccessException e){
            log.error("Redis空值保存失败，题目ID={}", id, e);
        }
    }

    // 世代号加 1，同时删除缓存
    @Override
    public void evictAndIncrementVersion(Long id) {
        try{
            redisTemplate.execute(
                    INVALIDATE,
                    List.of(versionKey(id), PREFIX + id)
            );
        } catch (DataAccessException e){
            log.error("Redis删除失败，题目ID={}", id, e);
        }
    }
    @Override
    public void evict(Long id) {
        try{
            redisTemplate.delete(PREFIX + id);
        } catch (DataAccessException e){
            log.error("Redis删除失败，题目ID={}", id, e);
        }

    }

    @Override
    public String get(Long id) {
        try{
            return redisTemplate.opsForValue().get(PREFIX + id);
        } catch (DataAccessException e){
            log.error("Redis读取失败，题目ID={}", id, e);
            return null;
        }
    }
}

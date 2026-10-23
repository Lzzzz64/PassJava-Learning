package org.example.passjava.question.service.impl;

import org.example.passjava.question.service.QmsQuestionCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class QmsQuestionCacheServiceImpl implements QmsQuestionCacheService {
    private static final Logger log =
            LoggerFactory.getLogger(QmsQuestionCacheServiceImpl.class);

    private static final String PREFIX =
            "passjava:question:detail:";

    @Autowired
    private StringRedisTemplate redisTemplate;

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

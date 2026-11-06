package org.example.passjava.question.service;
import org.springframework.stereotype.Service;


public interface QmsQuestionCacheService {
    String NULL_VALUE = "#NULL#";

    // 获取题目缓存的世代号
    String getGeneration(Long id);

    // 获取题目缓存
    String get(Long id);

    // 如果缓存与世代号一致，则保存题目缓存
    boolean saveIfUnchanged(
            Long id,
            String expectedGeneration,
            String json);

    // 保存题目缓存
    void save(Long id, String json);

    // 如果缓存与世代号一致，则缓存空值
    boolean saveNullIfUnchanged(Long id, String expectedGeneration);

    // 缓存空值
    void saveNull(Long id);

    // 世代号加 1，同时删除题目缓存
    void evictAndIncrementVersion(Long id);

    // 删除题目缓存
    void evict(Long id);
}

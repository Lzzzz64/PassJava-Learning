package org.example.passjava.question.service;
import org.springframework.stereotype.Service;


public interface QmsQuestionCacheService {
    String NULL_VALUE = "#NULL#";

    // 获取题目缓存
    String get(Long id);

    // 保存题目缓存
    void save(Long id, String json);

    // 缓存空值
    void saveNull(Long id);

    // 删除题目缓存
    void evict(Long id);
}

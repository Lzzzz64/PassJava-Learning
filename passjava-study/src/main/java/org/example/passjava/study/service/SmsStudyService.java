package org.example.passjava.study.service;

import org.example.passjava.study.VO.SmsStudyRecordVO;

import java.util.List;

public interface SmsStudyService {
    void recordView(Long userId, Long questionId);
    List<SmsStudyRecordVO> getViews(Long userId);

    void addFavorite(Long userId, Long questionId);
    void removeFavorite(Long userId, Long questionId);
    List<Long> getFavorites(Long userId);

    void markWrong(Long userId, Long questionId);
    void removeWrong(Long userId, Long questionId);
    List<SmsStudyRecordVO> getWrongs(Long userId);
}

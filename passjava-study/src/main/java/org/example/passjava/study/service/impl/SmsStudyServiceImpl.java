package org.example.passjava.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.passjava.study.VO.SmsStudyRecordVO;
import org.example.passjava.study.entity.SmsFavorite;
import org.example.passjava.study.entity.SmsViewLog;
import org.example.passjava.study.entity.SmsWrongQuestion;
import org.example.passjava.study.mapper.SmsFavoriteMapper;
import org.example.passjava.study.mapper.SmsViewLogMapper;
import org.example.passjava.study.mapper.SmsWrongQuestionMapper;
import org.example.passjava.study.mq.StudyEventPublisher;
import org.example.passjava.study.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class SmsStudyServiceImpl implements SmsStudyService {
    @Autowired
    private SmsFavoriteMapper smsFavoriteMapper;
    @Autowired
    private SmsViewLogMapper smsViewLogMapper;
    @Autowired
    private SmsWrongQuestionMapper smsWrongQuestionMapper;
    @Autowired
    private QmsQuestionCheckService qmsQuestionCheckService;
    @Autowired
    private StudyEventPublisher studyEventPublisher;
    @Override
    public void recordView(Long userId, Long questionId) {
        qmsQuestionCheckService.checkExists(questionId);
        int i = smsViewLogMapper.recordView(userId, questionId);
        if(i < 1)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "记录失败");
        studyEventPublisher.publishView(userId, questionId);
    }

    @Override
    public List<SmsStudyRecordVO> getViews(Long userId) {
        LambdaQueryWrapper<SmsViewLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SmsViewLog::getUserId, userId).orderByDesc(SmsViewLog::getLastViewTime).last("LIMIT 100");
        List<SmsViewLog> smsViewLogList = smsViewLogMapper.selectList(lambdaQueryWrapper);

        if(smsViewLogList == null || smsViewLogList.size() == 0){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "没有相关记录");
        }

        List<SmsStudyRecordVO> smsStudyRecordVOList = new ArrayList<>();
        for(SmsViewLog smsViewLog : smsViewLogList){
            SmsStudyRecordVO smsStudyRecordVO = new SmsStudyRecordVO();
            smsStudyRecordVO.setQuestionId(smsViewLog.getQuestionId());
            smsStudyRecordVO.setCount(smsViewLog.getViewCount());
            smsStudyRecordVO.setTime(smsViewLog.getLastViewTime());
            smsStudyRecordVOList.add(smsStudyRecordVO);
        }
        return smsStudyRecordVOList;
    }

    @Override
    public void addFavorite(Long userId, Long questionId) {
        qmsQuestionCheckService.checkExists(questionId);
        int i = smsFavoriteMapper.addFavorite(userId, questionId);
        if(i < 1)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "添加失败");
    }

    @Override
    public void removeFavorite(Long userId, Long questionId) {
        qmsQuestionCheckService.checkExists(questionId);
        int i = smsFavoriteMapper.delete(new LambdaQueryWrapper<SmsFavorite>()
                .eq(SmsFavorite::getUserId, userId)
                .eq(SmsFavorite::getQuestionId, questionId));
        if(i != 1)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "移除失败");
    }

    /**
     * 获取用户收藏的题目列表
     * @param userId
     * @return
     */
    @Override
    public List<Long> getFavorites(Long userId) {
        LambdaQueryWrapper<SmsFavorite> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SmsFavorite::getUserId, userId);
        List<SmsFavorite> smsFavoriteList = smsFavoriteMapper.selectList(lambdaQueryWrapper);
        if(smsFavoriteList == null || smsFavoriteList.size() == 0){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "没有相关记录");
        }
        List<Long> questionIdList = new ArrayList<>();
        for(SmsFavorite smsFavorite : smsFavoriteList){
            questionIdList.add(smsFavorite.getQuestionId());
        }
        return questionIdList;
    }

    @Override
    public void markWrong(Long userId, Long questionId) {
        qmsQuestionCheckService.checkExists(questionId);
        int i = smsWrongQuestionMapper.markWrong(userId, questionId);
        if(i < 1)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "标记失败");
    }

    @Override
    public void removeWrong(Long userId, Long questionId) {
        qmsQuestionCheckService.checkExists(questionId);
        int i = smsWrongQuestionMapper.delete(new LambdaQueryWrapper<SmsWrongQuestion>()
                .eq(SmsWrongQuestion::getUserId, userId)
                .eq(SmsWrongQuestion::getQuestionId, questionId));
        if(i != 1)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "移除失败");

    }

    @Override
    public List<SmsStudyRecordVO> getWrongs(Long userId) {
        LambdaQueryWrapper<SmsWrongQuestion> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SmsWrongQuestion::getUserId, userId).orderByDesc(SmsWrongQuestion::getLastWrongTime).last("LIMIT 100");
        List<SmsWrongQuestion> smsWrongQuestionList = smsWrongQuestionMapper.selectList(lambdaQueryWrapper);
        if(smsWrongQuestionList == null || smsWrongQuestionList.size() == 0){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "没有相关记录");
        }
        List<SmsStudyRecordVO> smsStudyRecordVOList = new ArrayList<>();
        for(SmsWrongQuestion smsWrongQuestion : smsWrongQuestionList){
            SmsStudyRecordVO smsStudyRecordVO = new SmsStudyRecordVO();
            smsStudyRecordVO.setQuestionId(smsWrongQuestion.getQuestionId());
            smsStudyRecordVO.setCount(smsWrongQuestion.getWrongCount());
            smsStudyRecordVO.setTime(smsWrongQuestion.getLastWrongTime());
            smsStudyRecordVOList.add(smsStudyRecordVO);
        }
        return smsStudyRecordVOList;
    }
}

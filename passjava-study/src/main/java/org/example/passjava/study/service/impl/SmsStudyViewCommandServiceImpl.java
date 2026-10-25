package org.example.passjava.study.service.impl;

import org.example.passjava.study.entity.SmsEventOutbox;
import org.example.passjava.study.mapper.SmsEventOutboxMapper;
import org.example.passjava.study.mapper.SmsViewLogMapper;
import org.example.passjava.study.service.QmsQuestionCheckService;
import org.example.passjava.study.service.SmsEventOutboxService;
import org.example.passjava.study.service.SmsStudyViewCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.UUID;

@Service
public class SmsStudyViewCommandServiceImpl implements SmsStudyViewCommandService {
    @Autowired
    private SmsViewLogMapper smsViewLogMapper;
    @Autowired
    private QmsQuestionCheckService qmsQuestionCheckService;
    @Autowired
    private SmsEventOutboxMapper smsEventOutboxMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordAndCreateEvent(Long userId, Long questionId) {

        // 1. 检查问题是否存在
        qmsQuestionCheckService.checkExists(questionId);

        // 2. 记录用户查看问题
        int i = smsViewLogMapper.recordView(userId, questionId);

        if (i < 1) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "记录失败");
        }

        // 3. 创建待发送事件
        SmsEventOutbox smsEventOutbox = new SmsEventOutbox();

        Date now = new Date();

        smsEventOutbox.setEventId(UUID.randomUUID().toString());
        smsEventOutbox.setUserId(userId);
        smsEventOutbox.setQuestionId(questionId);
        smsEventOutbox.setOccurredAt(now);

        smsEventOutbox.setStatus(0);
        smsEventOutbox.setRetryCount(0);
        smsEventOutbox.setNextRetryAt(now);

        // 4. 保存 Outbox 事件
        int result = smsEventOutboxMapper.insert(smsEventOutbox);

        if (result != 1) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "事件保存失败");
        }
    }
}

package org.example.passjava.study.service.impl;

import org.example.passjava.study.entity.SmsDailyStat;
import org.example.passjava.study.entity.SmsStudyViewEvent;
import org.example.passjava.study.mapper.SmsDailyStatMapper;
import org.example.passjava.study.mapper.SmsProcessedEventMapper;
import org.example.passjava.study.service.SmsStudyEventProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SmsStudyEventProcessServiceImpl implements SmsStudyEventProcessService {
    @Autowired
    private SmsDailyStatMapper smsDailyStatMapper;
    @Autowired
    private SmsProcessedEventMapper smsProcessedEventMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void process(SmsStudyViewEvent event) {
        //1.检查事件是否已经处理过
        int i = smsProcessedEventMapper.insertIfAbsent(event.getEventId());
        if (i == 0) {
            return;
        }
        //2.处理事件
        smsDailyStatMapper.increase(event.getUserId(), event.getOccurredAt());
    }
}

package org.example.passjava.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.passjava.study.entity.SmsProcessedEvent;
import org.example.passjava.study.service.SmsProcessedEventService;
import org.example.passjava.study.mapper.SmsProcessedEventMapper;
import org.springframework.stereotype.Service;

/**
* @author 123456
* @description 针对表【sms_processed_event】的数据库操作Service实现
* @createDate 2026-10-24 12:09:19
*/
@Service
public class SmsProcessedEventServiceImpl extends ServiceImpl<SmsProcessedEventMapper, SmsProcessedEvent>
    implements SmsProcessedEventService{

}





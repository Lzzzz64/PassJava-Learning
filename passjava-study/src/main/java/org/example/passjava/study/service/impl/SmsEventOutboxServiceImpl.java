package org.example.passjava.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.passjava.study.entity.SmsEventOutbox;
import org.example.passjava.study.service.SmsEventOutboxService;
import org.example.passjava.study.mapper.SmsEventOutboxMapper;
import org.springframework.stereotype.Service;

/**
* @author 123456
* @description 针对表【sms_event_outbox】的数据库操作Service实现
* @createDate 2026-10-24 19:28:08
*/
@Service
public class SmsEventOutboxServiceImpl extends ServiceImpl<SmsEventOutboxMapper, SmsEventOutbox>
    implements SmsEventOutboxService{

}





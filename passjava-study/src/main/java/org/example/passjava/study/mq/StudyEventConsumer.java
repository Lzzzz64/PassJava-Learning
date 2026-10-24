package org.example.passjava.study.mq;

import com.rabbitmq.client.Channel;
import org.example.passjava.study.config.RabbitConfig;
import org.example.passjava.study.entity.SmsStudyViewEvent;
import org.example.passjava.study.service.SmsProcessedEventService;
import org.example.passjava.study.service.SmsStudyEventProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StudyEventConsumer {
    private static final Logger log =
            LoggerFactory.getLogger(StudyEventConsumer.class);
    @Autowired
    private SmsStudyEventProcessService smsStudyEventProcessService;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void consume(
            SmsStudyViewEvent event,
            Message message,
            Channel channel) throws IOException {

        long tag = message.getMessageProperties()
                .getDeliveryTag();

        try {
            // 业务执行完成时，数据库事务已提交
            smsStudyEventProcessService.process(event);

            // 手动确认本条消息
            channel.basicAck(tag, false);

            log.info("学习事件消费成功，eventId={}", event.getEventId());

        } catch (Exception ex) {
            log.error("学习事件处理失败，eventId={}",
                    event.getEventId(), ex);

            // 拒绝并且不重新入队，进入死信队列
            channel.basicNack(tag, false, false);
        }
    }


}

package org.example.passjava.study.mq;

import org.example.passjava.study.config.RabbitConfig;
import org.example.passjava.study.entity.SmsEventOutbox;
import org.example.passjava.study.entity.SmsStudyViewEvent;
import org.example.passjava.study.mapper.SmsEventOutboxMapper;
import org.example.passjava.study.service.SmsEventOutboxService;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class OutboxPublisher {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StudyEventPublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SmsEventOutboxMapper smsEventOutboxMapper;


    @Scheduled(fixedDelay = 5000)
    public void publishPending(){
        // 恢复超时消息
        smsEventOutboxMapper.recoverTimedOut();

        List<SmsEventOutbox> pendings = smsEventOutboxMapper.findPending();
        for(SmsEventOutbox pending : pendings){
            // 只有一个发布器能够成功领取该事件
            int i = smsEventOutboxMapper.claim(pending.getEventId());
            if (i == 0) continue;


            // 重新读取领取后的重试次数
            SmsEventOutbox event = smsEventOutboxMapper.selectById(pending.getEventId());

            try{
                // 创建事件对象
                SmsStudyViewEvent message = new SmsStudyViewEvent(
                        event.getEventId(),
                        event.getUserId(),
                        event.getQuestionId(),
                        new Date());

                // 发送事件
                CorrelationData correlation = new CorrelationData(message.getEventId());
                rabbitTemplate.convertAndSend(
                        RabbitConfig.EXCHANGE,
                        RabbitConfig.ROUTING_KEY,
                        message,
                        rabbitMessage -> {
                            rabbitMessage.getMessageProperties()
                                    .setDeliveryMode(
                                            MessageDeliveryMode.PERSISTENT);
                            return rabbitMessage;
                        }, //将消息设置为持久化消息
                        correlation);

                // 等待 Broker 确认
                CorrelationData.Confirm confirm =
                        correlation.getFuture()
                                .get(5, TimeUnit.SECONDS);

                if (!confirm.isAck()) {
                    throw new IllegalStateException(
                            "Broker 接收消息失败: " + confirm.getReason());
                }

                // 已确认到达 Broker，但可能没有匹配的队列
                if (correlation.getReturned() != null) {
                    throw new IllegalStateException(
                            "消息无法路由: " +
                                    correlation.getReturned().getReplyText());
                }

                smsEventOutboxMapper.markSent(message.getEventId());

                log.info("Outbox发送成功，eventId={}",
                        event.getEventId());
            }catch (Exception e){
                int retryCount = event.getRetryCount();
                int status = retryCount >= 3 ? 3 : 0;
                long delaySeconds =
                        Math.min(300, 5L * (1L << Math.min(retryCount, 6)));
                Date nextRetryAt = new Date(System.currentTimeMillis() + delaySeconds);
                String error = e.toString();
                if(error.length() > 500)
                    error = error.substring(0, 500);
                smsEventOutboxMapper.markFailure(
                        event.getEventId(),
                        status,
                        nextRetryAt,
                        error);

                log.error("学习事件发送失败，eventId={}", event.getEventId(), e);
            }
        }
    }
}

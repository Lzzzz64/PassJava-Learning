package org.example.passjava.study.mq;

import org.example.passjava.study.config.RabbitConfig;
import org.example.passjava.study.entity.SmsStudyViewEvent;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class StudyEventPublisher {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StudyEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public StudyEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;

        this.rabbitTemplate.setConfirmCallback(
                (correlationData, ack, cause) -> {

                    if (ack) {
                        System.out.println("消息成功到达交换机");
                    } else {
                        System.out.println("消息发送失败：" + cause);
                    }
                }
        );
    }

    public void publishView(Long userId, Long questionId){
        SmsStudyViewEvent studyEvent = new SmsStudyViewEvent(
                UUID.randomUUID().toString(),
                userId, questionId, new Date());

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                studyEvent,
                new CorrelationData(studyEvent.getEventId()));

        log.info("学习事件已提交发送，eventId={}", studyEvent.getEventId());
    }
}

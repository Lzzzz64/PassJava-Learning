
package org.example.passjava.study.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE =
            "passjava.study.exchange";

    public static final String QUEUE =
            "passjava.study.view.queue";

    public static final String ROUTING_KEY = "study.view";

    public static final String DLX =
            "passjava.study.dlx";

    public static final String DEAD_QUEUE =
            "passjava.study.dead.queue";

    @Bean
    public DirectExchange studyExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX, true, false);
    }

    @Bean
    public Queue studyQueue() {
        return QueueBuilder.durable(QUEUE)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey("study.dead")
                .build();
    }

    @Bean
    public Queue deadQueue() {
        return QueueBuilder.durable(DEAD_QUEUE).build();
    }

    @Bean
    public Binding studyBinding() {
        return BindingBuilder.bind(studyQueue())
                .to(studyExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding deadBinding() {
        return BindingBuilder.bind(deadQueue())
                .to(deadLetterExchange())
                .with("study.dead");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

package com.frank.gramturmq.rmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ配置信息.
 *
 * @author 张孝党 2019/12/23.
 * @version V0.0.1.
 * <p>
 * 更新履历： V0.0.1 2019/12/23 张孝党 创建.
 */
@Slf4j
@Configuration
public class RmqConfig {

    /**
     * 队列.
     */
    @Bean
    Queue topicQueue() {
        return new Queue(RmqConst.TOPIC_QUEUE);
    }


    /**
     * 注册exchange.
     */
    @Bean
    TopicExchange topicExchange() {
        log.info("注册exchange完成....");
        return new TopicExchange(RmqConst.QUEUE_NAME_TURNITIN_CLIENT);
    }

    /**
     * 建立绑定关系.
     */
    @Bean
    public Binding topicBinding() {
        return BindingBuilder.bind(topicQueue()).to(topicExchange()).with(RmqConst.TOPIC_QUEUE);
    }
}

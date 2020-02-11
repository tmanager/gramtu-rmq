package com.frank.gramturmq.rmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ服务类.
 *
 * @author 张孝党 2019/12/23.
 * @version V0.0.1.
 * <p>
 * 更新履历： V0.0.1 2019/12/23 张孝党 创建.
 */
@Slf4j
@Service
public class RmqService {

    // 注册rabbitmq
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送exchange消息.
     */
    public Object rpcToTurnitin(String msg) {
        log.info("*****************发送给grammarly的消息为：[{}]*****************", msg);

        // 发送消息
        Object response = this.rabbitTemplate.convertSendAndReceive(RmqConst.QUEUE_NAME_TURNITIN_CLIENT, msg);
        log.info("*****************发送给grammarly的返回消息为：[{}]*****************", response);

        return response;
        //this.rabbitTemplate.convertAndSend(RmqConst.QUEUE_NAME_GRAMMARLY_CLIENT, msg);
    }
}

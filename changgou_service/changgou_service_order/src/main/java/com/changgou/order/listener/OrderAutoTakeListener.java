package com.changgou.order.listener;

import com.changgou.order.service.OrderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 执行自动收货的的MQ监听器
 */
@Component
public class OrderAutoTakeListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderService orderService;

    @KafkaListener(topics = "order_take")
    public void msgHandle(ConsumerRecord<String,String> consumerRecord){
        logger.info("开始执行自动收货了.......");
        orderService.autoTake();
        logger.info("执行自动收货完毕.......");
    }
}

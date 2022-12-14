package com.changgou.order.listener;

import com.changgou.order.service.OrderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 用来执行超时未支付的订单关系的MQ监听器
 */
@Component
public class OrderCloseListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderService orderService;

    @KafkaListener(topics = "order_close")
    public void msgHandle(ConsumerRecord<String,String> consumerRecord){
        String orderId = consumerRecord.value();

        logger.info("开始调用订单关闭，orderId:{}",orderId );


        try {
            // 调用执行关闭
            orderService.closeOrder(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("调用订单结束，orderId:{}",orderId );
    }
}

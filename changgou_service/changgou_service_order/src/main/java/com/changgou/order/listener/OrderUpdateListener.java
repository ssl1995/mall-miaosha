package com.changgou.order.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.order.service.OrderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用于处理微信支付回调结果更新订单的MQ监听器
 */
@Component
public class OrderUpdateListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderService orderService;

    @KafkaListener(topics = "pay_order")
    public void msgHandle(ConsumerRecord<String,String> consumerRecord){
        Map<String,String> payOrderMap = JSON.parseObject(consumerRecord.value(), Map.class);
        String orderId = payOrderMap.get("outTradeNo");
        String transactionId = payOrderMap.get("transactionId");
        logger.info("开始调用微信支付订单的回调数据更新! 参数信息[orderId={}, transactionId={}]",orderId, transactionId);
        try {
            orderService.updateOrder(orderId, transactionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("调用微信支付订单的回调数据更新完毕! 参数信息[orderId={}, transactionId={}]",orderId, transactionId);
    }
}

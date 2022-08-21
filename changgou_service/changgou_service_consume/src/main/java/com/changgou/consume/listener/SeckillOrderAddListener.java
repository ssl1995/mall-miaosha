package com.changgou.consume.listener;


import com.alibaba.fastjson.JSON;
import com.changgou.consume.service.SeckillOrderService;
import com.changgou.seckill.pojo.SeckillOrder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 秒杀订单数据更新的MQ消费者监听器
 */
@Component
public class SeckillOrderAddListener {


    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 消息队列，异步去扣减库存
     */
    @KafkaListener(topics = "seckill_order")
    public void msgHandle(ConsumerRecord<String, String> consumerRecord) {

        String seckillOrderJSON = new String(consumerRecord.value());
        SeckillOrder seckillOrder = JSON.parseObject(seckillOrderJSON, SeckillOrder.class);
        //调用service层进行数据库表的秒杀订单创建以及秒杀商品剩余库存更新
        boolean updateResult = false;
        try {
            updateResult = seckillOrderService.add(seckillOrder) > 0 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

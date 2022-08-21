package com.changgou.listener;

import com.changgou.entity.Constants;
import com.changgou.service.PageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 生成商品详情页的消费者监听器
 */
@Component
public class CreatePageHtmlListener {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private PageService pageService;


    @KafkaListener(topics = Constants.GOODS_UP_TOPIC)
    public void msgHandle(ConsumerRecord<String,String> consumerRecord){
        String spuId = consumerRecord.value();
        logger.info("开始生成静态页面，spuId:{}",spuId);
        pageService.createPageHtml(spuId);
        logger.info("生成静态页面完成，spuId:{}",spuId);
    }
}

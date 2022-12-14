package com.changgou.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoTakeTask {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //这里配置为1分钟，是为了方便测试的。生产的时候应该设置为每日执行，比如每日凌晨三点：0 0 3 * * ?
    @Scheduled(cron = "0 0/1 * * * ?")
    public void autoTake(){
        kafkaTemplate.send("order_take", "-");
    }
}

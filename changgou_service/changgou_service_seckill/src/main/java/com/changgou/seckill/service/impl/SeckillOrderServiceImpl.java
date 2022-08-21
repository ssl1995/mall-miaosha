package com.changgou.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Constants;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.util.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Resource
    private SeckillOrderMapper seckillOrderMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IdWorker idWorker;


    @Override
    public boolean add(String username, String time, Long id) {
        //1.流量攻击屏蔽（同一个用户针对同一个订单，瞬间或者极短时间内连续（并发）下单）。或者成为恶意刷单！
        //  1.1 使用分布式锁的方式进行控制。由于incr在redis中是原子性自增，那么从一个默认值开始第一次自增时，如果出现多个线程同时对
        //      Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(Constants.SECKILL_USER_KEY + username + "_" + id, "1", 5, TimeUnit.SECONDS);
        //  1.2 同一个key进行自增，只有一个线程能将结果自增为1，其他线程自增后都是1+
        Long increment = redisTemplate.opsForValue().increment(Constants.SECKILL_USER_KEY + username + "_" + id);
        if (increment != 1) {
            logger.error("恶意刷流量，拒绝下单！username:{}, id:{}", username, id);
            return false;
        }
        //用户5秒之内只能下单一次
        redisTemplate.expire(Constants.SECKILL_USER_KEY + username + "_" + id, 5, TimeUnit.SECONDS);

        //2.针对同一个商品，用户多次下单，应该屏蔽.
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(username);
        seckillOrder.setSeckillId(id);
        //TODO 此处实际上可以再次优化性能
        int count = seckillOrderMapper.selectCount(seckillOrder);
        if (count > 0) {
            logger.error("不能多次下单！username:{}, id:{}", username, id);
            return false;
        }

        //3.秒杀商品列表中，获取是否是当前时间段的秒杀商品：根据秒杀商品ID从缓存查找商品
        // seckill_goods_20220820 商品id，能不能查到商品信息
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(Constants.SECKILL_GOODS_KEY + time).get(id);
        if (seckillGoods == null) {
            logger.error("商品不存在！username:{}, id:{}", username, id);
            return false;
        }

        //4.秒杀商品库存，根据秒杀商品ID从缓存中查找剩余库存数量，并判断
        // seckill_goods_stock_count_商品id 库存
        String stockCount = (String) redisTemplate.opsForValue().get(Constants.SECKILL_GOODS_STOCK_COUNT_KEY + id);
        if (stockCount == null || Integer.valueOf(stockCount) <= 0) {
            logger.error("商品售罄！username:{}, id:{}", username, id);
            return false;
        }

        //5.缓存中预减少商品库存，减少秒杀商品对应的库存数量
        Long decrement = redisTemplate.opsForValue().decrement(Constants.SECKILL_GOODS_STOCK_COUNT_KEY + id);
        if (decrement < 0) {
            redisTemplate.boundHashOps(Constants.SECKILL_GOODS_KEY + time).delete(id); //从缓存HASH中删除秒杀商品
            redisTemplate.delete(Constants.SECKILL_GOODS_STOCK_COUNT_KEY + id); //从缓存中删除商品库存结构
            logger.error("商品数据超卖！username:{}, id:{}", username, id);
            return false;
        }

        //6.将当期下单的订单信息，存入MQ
        SeckillOrder seckillOrderDB = new SeckillOrder();
        seckillOrderDB.setId(idWorker.nextId());// 雪花算法生成的id
        seckillOrderDB.setUserId(username); //消费者用户
        seckillOrderDB.setSeckillId(id);//秒杀商品ID
        seckillOrderDB.setSellerId(seckillGoods.getSellerId());//商家ID
        seckillOrderDB.setStatus("0");//未支付
        seckillOrderDB.setMoney(seckillGoods.getCostPrice());//秒杀价格

        kafkaTemplate.send("seckill_order", JSON.toJSONString(seckillOrderDB));

        return true;
    }
}

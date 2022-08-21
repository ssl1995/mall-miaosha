package com.changgou.consume.service;

import com.changgou.seckill.pojo.SeckillOrder;

public interface SeckillOrderService {


    /**
     * 秒杀订单数据更新
     * @param seckillOrder
     * @return
     */
    int add(SeckillOrder seckillOrder);
}

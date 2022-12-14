package com.changgou.consume.service;

import com.changgou.consume.dao.SeckillGoodsMapper;
import com.changgou.consume.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class SeckillOrderServiceImpl implements  SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;


    @Transactional
    @Override
    public int add(SeckillOrder seckillOrder) {
        seckillOrder.setCreateTime(new Date());
        //新增秒杀订单信息
        int c1 = seckillOrderMapper.insertSelective(seckillOrder);
        if(c1==0){
            return c1;
        }

        //更新秒杀商品剩余库存数量
        return seckillGoodsMapper.decr(seckillOrder.getSeckillId());
    }
}

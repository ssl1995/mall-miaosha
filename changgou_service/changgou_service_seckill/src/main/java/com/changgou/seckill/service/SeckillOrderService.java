package com.changgou.seckill.service;

public interface SeckillOrderService {

    /**
     * 秒杀下单
     * @param username 登录用户名
     * @param time  菜单时间
     * @param id 秒杀商品的Id
     * @return
     */
    boolean add(String username, String time, Long id);
}

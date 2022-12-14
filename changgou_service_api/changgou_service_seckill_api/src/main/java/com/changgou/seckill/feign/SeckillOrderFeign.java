package com.changgou.seckill.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "seckill")
@RequestMapping("/seckillorder")
public interface SeckillOrderFeign {

    @GetMapping("/add")
    public Boolean add(@RequestParam("time") String time, @RequestParam("id") Long id);
}

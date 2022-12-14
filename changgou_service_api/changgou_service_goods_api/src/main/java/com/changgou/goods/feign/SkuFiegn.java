package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Feign内部定义的所有接口，方法头都是来自于对应controller里的方法头（复制过来）
 */
@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFiegn {


    @GetMapping("/findBySpuId/{spuId}")
    public List<Sku> findBySpuId(@PathVariable("spuId") String spuId);

    @GetMapping
    public List<Sku> findAll();

    @GetMapping("/{id}")
    public Sku findById(@PathVariable("id") String id);

    @PostMapping("/decrCount")
    public Boolean decrCount(@RequestParam("skuId") String skuId, @RequestParam("num") Integer num);

    @PostMapping("/incrCount")
    public Boolean incrCount(@RequestParam("skuId") String skuId,@RequestParam("num") Integer num);
}

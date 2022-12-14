package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SpuService {

    /***
     * 查询所有
     * @return
     */
    List<Spu> findAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Spu findById(String id);

    /***
     * 新增
     * @param spu
     */
    void add(Spu spu);

    /***
     * 修改
     * @param spu
     */
    void update(Spu spu);

    /***
     * 删除
     * @param id
     */
    void delete(String id);

    /***
     * 多条件搜索
     * @param searchMap
     * @return
     */
    List<Spu> findList(Map<String, Object> searchMap);

    /***
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(int page, int size);

    /***
     * 多条件分页查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(Map<String, Object> searchMap, int page, int size);


    /**
     * 新增商品spu和sku
     * @param goods
     */
    void addGoods(Goods goods);


    /**
     * 根据spuId查询商品（包含spu和sku）
     * @param spuId
     * @return
     */
    Goods findBySpuId(String spuId);

    /**
     * 全量更新商品数据
     * @param goods
     */
    void updateGoods(Goods goods);


    /**
     * 根据主键ID审核商品
     * @param spuId
     */
    void auditGoods(String spuId);


    /**
     * 根据主键ID商品商品
     * @param spuId
     */
    void upGoods(String spuId);


    /**
     * 根据主键ID商品商品
     * @param spuId
     */
    void downGoods(String spuId);


    /**
     * 根据主键ID逻辑删除商品
     * @param spuId
     */
    void deleteGoodsLogic(String spuId);


    /**
     * 根据主键ID恢复商品
     * @param spuId
     */
    void restoreGoods(String spuId);


    /**
     * 物理删除商品
     * @param spuId
     */
    void deleteGoods(String spuId);
}

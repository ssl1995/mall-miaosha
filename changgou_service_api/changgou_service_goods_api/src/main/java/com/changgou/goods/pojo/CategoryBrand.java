package com.changgou.goods.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "tb_category_brand")
public class CategoryBrand implements Serializable {

    @Id
    private Integer brandId;

    @Id
    private Integer categoryId;


    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}

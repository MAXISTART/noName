package com.school.store.admin.good.entity;

import java.math.BigDecimal;

import com.school.store.admin.buy.entity.BuyOrder;
import com.school.store.admin.store.entity.StoreItem;
import com.school.store.annotation.CascadeDelete;
import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.*;


/**
    * good_items 物品表，纪录物品的基础属性
    * Thu Jan 25 15:17:21 CST 2018 MaXiStar
    */ 

@Entity
@Table(name = "good_items")
@Data
@CascadeDelete(value = StoreItem.class, filter = "goodId=?", args = {"id"})
public class GoodItem extends BaseEntity {

	// 物品的名称
	@Column(name = "name" , length = 255)
	private String name;

	// 物品的种类
	@Column(name = "sort" , length = 255)
	private String sort;

	// 物品的单位
	@Column(name = "unit" , length = 36)
	private String unit;

    // 物品的入库理论单价
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    // 采购的物品的规格
    @Column(name = "spec", length = 255)
    private String spec;

	// 刚开始的入库数量 ， 管理员不修改数量的话默认为 0
	@Column(name = "number", precision = 10, scale = 2)
	private BigDecimal number;

}


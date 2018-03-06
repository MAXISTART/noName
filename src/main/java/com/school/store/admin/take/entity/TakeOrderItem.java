package com.school.store.admin.take.entity;

import java.math.BigDecimal;

import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.*;


/**
    * take_order_items 申领申请表的明细
    * Thu Jan 25 15:17:21 CST 2018 MaXiStar
    */ 

@Entity
@Table(name = "take_order_items")
@Data
public class TakeOrderItem extends BaseEntity{

	// 申领的物品的ID,关联物品表的Id
	@Column(name = "goodId", length = 36)
	private String goodId;

	// 申领的物品的名称
	@Column(name = "name", length = 255)
	private String name;

	// 申领的物品的种类
	@Column(name = "sort", length = 255)
	private String sort;

	// 申领的物品的规格
	@Column(name = "spec", length = 255)
	private String spec;

	// 申领的物品的单位
	@Column(name = "unit", length = 36)
	private String unit;

	// 申领的物品的实际价格
	@Column(name = "price", precision = 10, scale = 2)
	private BigDecimal price;

	// 申领的物品的实际数量
	@Column(name = "number", length = 255)
	private Integer number;

	// 归还数量
	@Column(name = "returnNumber", length = 255)
	private Integer returnNumber;

	// 总单的ID
	@Column(name = "orderId", length = 36)
	private String orderId;


}


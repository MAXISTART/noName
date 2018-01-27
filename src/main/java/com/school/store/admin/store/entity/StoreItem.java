package com.school.store.admin.store.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.*;


/**
    * store_items 仓库明细表(统计用)
    * Thu Jan 25 15:17:21 CST 2018 MaXiStar
    */ 

@Entity
@Table(name = "store_items")
@Data
public class StoreItem extends BaseEntity{

	   // 申请时间
	   @Column(name = "name", length = 255)
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

	   // 物品的规格
	   @Column(name = "spec", length = 255)
	   private String spec;


	   // 入库的总额
	   @Column(name = "totalPrice", precision = 10, scale = 2)
	   private BigDecimal totalPrice;

	   // 入库时间
	   @Column(name = "inputTime")
	   @Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
	   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")   //Hibernate中@ResponseBody返回的时间格式
	   private Date inputTime;
}


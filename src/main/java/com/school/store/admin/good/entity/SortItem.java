package com.school.store.admin.good.entity;

import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
    * sort_items 物品种类表，纪录物品的种类名称
    * Thu Jan 25 15:17:21 CST 2018 MaXiStar
    */ 

@Entity
@Table(name = "sort_items")
@Data
public class SortItem extends BaseEntity {

	// 种类的名称
	@Column(name = "name" , length = 255)
	private String name;

}


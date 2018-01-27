package com.school.store.admin.buy.entity;

import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * buy_order_items 采购明细表
 * Thu Jan 25 15:17:21 CST 2018 MaXiStar
 */

@Entity
@Table(name = "buy_order_items")
@Data
public class BuyOrderItem extends BaseEntity {

    // 采购的物品的ID,关联物品表的Id
    @Column(name = "goodId", length = 36)
    private String goodId;

    // 采购的物品的名称
    @Column(name = "name", length = 255)
    private String name;

    // 采购的物品的种类
    @Column(name = "sort", length = 255)
    private String sort;

    // 采购的物品的规格
    @Column(name = "spec", length = 255)
    private String spec;

    // 采购的物品的单位
    @Column(name = "unit", length = 36)
    private String unit;

    // 采购的物品的实际价格
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    // 采购的物品的实际数量
    @Column(name = "number", length = 255)
    private Integer number;

    // 对应的总单ID
    @Column(name = "orderId", length = 36)
    private String orderId;

}


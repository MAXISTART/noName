package com.school.store.admin.store.entity;

import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * store_operation_items 仓库操作纪录明细表
 *                       这些记录都具有时效性，尽管物品升价了，原来的记录也不会改变他的纪录，并不是统计用的，而是展示历史数据用的，
 *                       所以所有数据都需要持久化到数据库
 * Thu Jan 25 15:17:21 CST 2018 MaXiStar
 */

@Entity
@Table(name = "store_operation_items")
@Data
public class StoreOperationItem extends BaseEntity {

    // 操作的物品的ID,关联物品表的Id
    @Column(name = "goodId", length = 36)
    private String goodId;

    // 操作的物品的名称
    @Column(name = "name", length = 255)
    private String name;

    // 操作的物品的种类
    @Column(name = "sort", length = 255)
    private String sort;

    // 操作的物品的规格
    @Column(name = "spec", length = 255)
    private String spec;

    // 操作的物品的单位
    @Column(name = "unit", length = 36)
    private String unit;

    // 操作的物品的实际价格
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    // 操作的物品的实际数量
    @Column(name = "number", length = 255)
    private BigDecimal number;

    @Column(name = "orderId", length = 36)
    private String orderId;

}


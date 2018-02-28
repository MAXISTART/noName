package com.school.store.admin.store.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.school.store.admin.good.entity.GoodItem;
import com.school.store.admin.refine.Refine;
import com.school.store.admin.refine.RefineMethod;
import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.*;


/**
 * store_items 仓库明细表(统计用)
 *              库存表是用来统计的，所以东西都是实时更新的，并不是一张记录表，跟物品表绑定关系而成的，物品的价格变了他也要跟着一起变
 *              除了入库时间和入库的物品ID外其余东西都是根据物品表去读取
 * Thu Jan 25 15:17:21 CST 2018 MaXiStar
 */

@Entity
@Table(name = "store_items")
@Data
public class StoreItem extends BaseEntity {

    // 物品表关联的ID
    @Column(name = "goodId", length = 36, unique = true)
    private String goodId;

    // 当前库存还有多少，这个是需要持久化到数据库里面去的
    @Column(name = "number", length = 255)
    private Integer number;

    // 当前正在审批的数量（像订场一样，被锁住的数量，也是有个时效，看审批流程的时效），默认为0
    @Column(name = "lockNumber", length = 255)
    private Integer lockNumber;

    // 存储物品的信息，但是只是展示的时候用的容器
    @Transient
    @Refine(value = RefineMethod.setGoodItem, argNames = {"goodId"})
    private GoodItem goodItem;

    // 入库的总额，这个是不用存进数据库的，而是每次展示的时候都进行运算
    @Transient
    private BigDecimal totalPrice;

    // 入库时间
    @Column(name = "inputTime")
    @Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")   //Hibernate中@ResponseBody返回的时间格式
    private Date inputTime;
}


package com.school.store.admin.take.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * take_orders 申领申请表
 * Thu Jan 25 15:17:21 CST 2018 MaXiStar
 */

@Entity
@Table(name = "take_orders")
@Data
public class TakeOrder extends BaseEntity{

    // 申领人员所属部门ID
    @Column(name = "departmentId", length = 36)
    private String departmentId;

    // 申领时间
    @Column(name = "outputTime")
    @Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")   //Hibernate中@ResponseBody返回的时间格式
    private Date outputTime;

    // 申领总额
    @Column(name = "outputTotalPrice", precision = 10, scale = 2)
    private BigDecimal outputTotalPrice;

    // 申领人员的ID
    @Column(name = "requestorId", length = 36)
    private String requestorId;

    // 申领具体说明(比如 原因事项)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 审批流程的任务ID，能查询当前审批情况
    @Column(name = "approvalTaskId", length = 36)
    private String approvalTaskId;

    // 审批结果，1表示通过，0表示不通过，2表示还未审核
    @Column(name = "approvalResult", length = 4)
    private Integer approvalResult;

    // 最后审批结果的时间
    @Column(name = "approvalTime")
    @Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")   //Hibernate中@ResponseBody返回的时间格式
    private Date approvalTime;

    // 明细内容
    @Transient
    private List<TakeOrderItem> takeOrderItems;

}


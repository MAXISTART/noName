package com.school.store.admin.store.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * store_operations 仓库操作纪录表
 * Thu Jan 25 15:17:20 CST 2018 MaXiStar
 */

@Entity
@Table(name = "store_operations")
@Data
public class StoreOperation extends BaseEntity {

    // 操作类型(比如1表示入库,2表示出库, 不同类型对应不同的业务逻辑)，详见业务设定表
    @Column(name = "type", length = 4)
    private Integer type;

    // 部门ID
    @Column(name = "departmentId", length = 36)
    private String departmentId;

    // 申请操作的时间
    @Column(name = "requestTime")
    @Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")   //Hibernate中@ResponseBody返回的时间格式
    private Date requestTime;

    // 申请操作的库存总价
    @Column(name = "requestTotalPrice", precision = 10, scale = 2)
    private BigDecimal requestTotalPrice;

    // 操作申请人Id
    @Column(name = "requestorId", length = 36)
    private String requestorId;

    // 操作申请原因
    @Column(name = "description",columnDefinition = "TEXT")
    private String description;

    // 操作审批人Id
    @Column(name = "responsorId", length = 36)
    private String responsorId;

    // 操作申请流程的任务ID
    @Column(name = "approvalTaskId", length = 36)
    private Integer approvalTaskId;

    // 审批结果，1表示通过，0表示不通过，2表示还未审核
    @Column(name = "approvalResult", length = 4)
    private Integer approvalResult;

    // 审批意见(按不同老师不同意见的json格式，比如 [ {id:"",name:"",opinion:""}, {}, {}])
    @Column(name = "opinion", columnDefinition = "TEXT")
    private String opinion;

    // 最后审批结果的时间
    @Column(name = "approvalTime")
    @Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")   //Hibernate中@ResponseBody返回的时间格式
    private Date approvalTime;

    // 明细内容
    @Transient
    private List<StoreOperationItem> storeOperationItems;
}


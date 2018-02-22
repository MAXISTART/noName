package com.school.store.admin.buy.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.school.store.base.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


/**
    * buy_orders 采购申请表
    * Thu Jan 25 15:17:21 CST 2018 MaXiStar
    */ 

@Entity
@Table(name = "buy_orders")
@Data
public class BuyOrder extends BaseEntity {

	// 采购人员所属部门ID
	@Column(name = "departmentId", length = 36)
	private String departmentId;

	// 申请时间
	@Column(name = "requestTime")
	@Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")   //Hibernate中@ResponseBody返回的时间格式
	private Date requestTime;

	// 申请采购的总额
	@Column(name = "requestTotalPrice", precision = 10, scale = 2)
	private BigDecimal requestTotalPrice;

	// 申请采购人员的ID
	@Column(name = "requestorId", length = 36)
	private String requestorId;

	// 申请采购的具体说明(比如采购的来源，商家的联系方式，原因事项)
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

	// 采购成功后的发票图片地址，有多张，格式为 {http:/.....},{},{}
	@Column(name = "bills", columnDefinition = "TEXT")
	private String bills;

	// 明细说明
	@Transient
	private List<BuyOrderItem> buyOrderItems;

	// 部门名称
	@Transient
	private String departmentName;

}


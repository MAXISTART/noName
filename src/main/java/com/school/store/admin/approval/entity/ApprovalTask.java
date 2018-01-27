package com.school.store.admin.approval.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.school.store.base.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


/**
    * approval_tasks 任务表
    * Thu Jan 25 15:17:21 CST 2018 MaXiStar
    */

@Entity
@Table(name = "approval_task")
@Data
public class ApprovalTask extends BaseEntity{

	// 该任务对应的节点id，和节点表相关联，是{1,3,5,7-8}这样的文本格式，varchar格式
	@Column(name = "taskNodeIds" , length = 255)
	private String taskNodeIds;

	// 任务当前进行的节点index
	@Column(name = "taskStageIndex" , length = 10)
	private Integer taskStageIndex;

	// 任务创建时间
	@Column(name = "taskCreateTime" )
	@Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")   //Hibernate中@ResponseBody返回的时间格式
	private Date taskCreateTime;

	// 任务结束时间
	@Column(name = "taskCompleteTime")
	@Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")   //Hibernate中@ResponseBody返回的时间格式
	private Date taskCompleteTime;

	// 任务当前状态，1-正在进行 2-已经结束 3-已经过期
	@Column(name = "taskState" , length = 4)
	private Integer taskState;

	// 任务结果， 1-通过 2-拒绝 3-过期
	@Column(name = "taskResult" , length = 4)
	private Integer taskResult;

	// 任务时间，表示的是天数
	@Column(name = "taskLife" , length = 255)
	private Integer taskLife;

	// 申请任务的用户ID
	@Column(name = "requestorId" , length = 36)
	private String requestorId;

	// 任务详情(非常重要)
	@Column(name = "description" , columnDefinition = "TEXT")
	private String description;

	// 申请任务的时间
	@Column(name = "requestorTime" )
	@Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")   //Hibernate中@ResponseBody返回的时间格式
	private Date requestorTime;

}


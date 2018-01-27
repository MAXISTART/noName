package com.school.store.admin.approval.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.school.store.base.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


/**
    * approval_task_nodes 任务节点表
    * Thu Jan 25 15:17:21 CST 2018 MaXiStar
    */ 

@Entity
@Table(name = "approval_task_nodes")
@Data
public class ApprovalTaskNode extends BaseEntity{

	// 所属任务的ID
	@Column(name = "taskId", length = 36)
	private String taskId;

	// 下一个任务节点的ID
	@Column(name = "nextNodeId", length = 36)
	private String nextNodeId;

	// 前一个任务节点的ID
	@Column(name = "previousNodeId", length = 36)
	private String previousNodeId;

	// 申请人ID
	@Column(name = "requestorId", length = 36)
	private String requestorId;

	// 申请时间
	@Column(name = "requestorTime")
	@Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")   //Hibernate中@ResponseBody返回的时间格式
	private Date requestorTime;

	// 申请内容
	@Column(name = "request_content", columnDefinition = "TEXT")
	private String requestContent;

	// 审批人ID
	@Column(name = "responsor", length = 36)
	private String responsor;

	// 审批结果（1-通过，2-拒绝， 3-过期）
	@Column(name = "response", length = 4)
	private Integer response;

	// 审批意见(如果是通过就不用写)
	@Column(name = "opinion", columnDefinition = "TEXT")
	private String opinion;

	// 节点状态（1-正在进行 2-结束)
	@Column(name = "taskNodeState", length = 4)
	private Integer taskNodeState;

}


package com.school.store.admin.department.entity;


import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.*;


/**
    * departments 部门表
    * Thu Jan 25 15:17:21 CST 2018 MaXiStar
    */ 

@Entity
@Table(name = "departments")
@Data
public class Department extends BaseEntity{

	// 部门名称
	@Column(name = "name" , length = 36)
	private String name;

	// 部门职能详细描述,默认是text类型
	@Column(name = "description" , columnDefinition = "TEXT")
	private String description;

	// 部门的主要负责人，与user表关联
	@Column(name = "responsorId" , length = 36)
	private String responsorId;

	// 部门的主要负责人的name，与user表关联
	@Transient
	private String responsorName;

}


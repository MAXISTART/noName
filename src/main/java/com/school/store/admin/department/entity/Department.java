package com.school.store.admin.department.entity;


import com.school.store.admin.buy.entity.BuyOrder;
import com.school.store.admin.refine.Refine;
import com.school.store.admin.refine.RefineMethod;
import com.school.store.admin.take.entity.TakeOrder;
import com.school.store.admin.user.entity.User;
import com.school.store.annotation.CascadeDelete;
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
// 如果这个部门被删除了，要先删了部门下的所有user，条件是filter（有时候删除是有多个条件的），参数是这个部门的id
@CascadeDelete(value = User.class, filter = "departmentId=?", args = {"id"})
@CascadeDelete(value = BuyOrder.class, filter = "departmentId=?", args = {"id"})
@CascadeDelete(value = TakeOrder.class, filter = "departmentId=?", args = {"id"})
public class Department extends BaseEntity{

	// 部门名称
	@Column(name = "name" , length = 36, unique = true)
	private String name;

	// 部门职能详细描述,默认是text类型
	@Column(name = "description" , columnDefinition = "TEXT")
	private String description;

	// 部门的主要负责人，与user表关联
	@Column(name = "responsorId" , length = 36)
	private String responsorId;

	// 部门的主要负责人的name，与user表关联
	@Transient
	@Refine(value = RefineMethod.setUserName, argNames = {"responsorId"})
	private String responsorName;

}


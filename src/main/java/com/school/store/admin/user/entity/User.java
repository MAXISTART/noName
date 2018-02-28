package com.school.store.admin.user.entity;


import com.school.store.admin.buy.entity.BuyOrder;
import com.school.store.admin.permission.entity.Permission;
import com.school.store.admin.permission.entity.UserToPermission;
import com.school.store.admin.refine.Refine;
import com.school.store.admin.refine.RefineMethod;
import com.school.store.admin.take.entity.TakeOrder;
import com.school.store.annotation.CascadeDelete;
import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;


/**
    * users 用户表
    * Thu Jan 25 15:17:21 CST 2018 MaXiStar
    */ 
@Entity
@Table(name = "users")
@Data
@CascadeDelete(value = UserToPermission.class, filter = "userId=?", args = {"id"})
@CascadeDelete(value = BuyOrder.class, filter = "requestorId=?", args = {"id"})
@CascadeDelete(value = TakeOrder.class, filter = "requestorId=?", args = {"id"})
public class User extends BaseEntity{

	// 用户的昵称
	@Column(name = "name" , length = 36)
	private String name;

	// 用户的密码
	@Column(name = "password", length = 36)
	private String password;

	// 用户的微信openid
	@Column(name = "openId" , length = 36)
	private String openId;

	// 用户的部门ID，对应部门表中的id
	@Column(name = "departmentId" , length = 36)
	private String departmentId;

	// 用户的电话号码
	@Column(name = "phoneNumber" , length = 36)
	private String phoneNumber;

	// 用户的qq号码
	@Column(name = "qqNumber" , length = 36)
	private String qqNumber;

	// 用户的邮箱信息
	@Column(name = "mailbox" , length = 36)
	private String mailbox;

	// 用户的微信号
	@Column(name = "weixin" , length = 36)
	private String weixin;

	// 用户的备注
	@Column(name = "description" , columnDefinition = "TEXT")
	private String description;

	@Transient
	@Refine(value = RefineMethod.setDepartmentName, argNames= {"departmentId"})
	// 填充方法是 setDepartmentName ， 需要传递的属性的名称为 departmentId，
	// 任何实体，只要这样标注，调用refine的方法后，这个departmentName属性就会根据setDepartmentName的方法被填充数据
	private String departmentName;

	// 该用户对应的所有权限
	@Transient
	@Refine(value = RefineMethod.setPermissions, argNames= {"id"})
	private Set<Permission> permissions;

}


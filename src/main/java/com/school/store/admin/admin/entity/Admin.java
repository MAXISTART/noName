package com.school.store.admin.admin.entity;

import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.*;


/**
 * admins 管理员表
 * Thu Jan 25 15:17:21 CST 2018 MaXiStar
 */


@Entity
@Table(name = "admins")
@Data
public class Admin extends BaseEntity {

     // 管理员的昵称
     @Column(name = "name" , length = 36)
     private String name;

     // 管理员的电话号码
     @Column(name = "phoneNumber" , length = 36)
     private String phoneNumber;

     // 管理员的备注
     @Column(name = "description" , columnDefinition = "TEXT")
     private String description;

}


package com.school.store.admin.permission.entity;

import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * permissions 权限的表，用来定义所有权限的
 * Thu Jan 25 15:17:21 CST 2018 MaXiStar
 */
@Entity
@Table(name = "permissions")
@Data
public class Permission extends BaseEntity {

    // 权限名称
    @Column(name = "name" , length = 36)
    private String name;

    // 权限描述
    @Column(name = "description" , columnDefinition = "TEXT")
    private String description;

}


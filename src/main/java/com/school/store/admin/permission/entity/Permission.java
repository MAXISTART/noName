package com.school.store.admin.permission.entity;

import com.school.store.annotation.CascadeDelete;
import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * permissions 权限的表，用来定义所有权限的
 * Thu Jan 25 15:17:21 CST 2018 MaXiStar
 */
@Entity
@Table(name = "permissions")
@Data
@CascadeDelete(value = UserToPermission.class, filter = "permissionId=?", args = {"id"})
public class Permission extends BaseEntity {

    @Transient
    private static final long serialVersionUID = 268075760018187388L;

    // 权限名称
    @Column(name = "name" , length = 36, unique = true)
    private String name;

    // 权限描述
    @Column(name = "description" , columnDefinition = "TEXT")
    private String description;

}


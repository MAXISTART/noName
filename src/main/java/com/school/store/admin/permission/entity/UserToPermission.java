package com.school.store.admin.permission.entity;

import com.school.store.base.model.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * permissions 用户权限对应表
 * Thu Jan 25 15:17:21 CST 2018 MaXiStar
 */
@Entity
@Table(name = "user_to_permissions")
@Data
public class UserToPermission extends BaseEntity {

    // 用户ID
    @Column(name = "userId" , length = 36, nullable = false)
    private String userId;

    // 权限ID
    @Column(name = "permissionId" , length = 36, nullable = false)
    private String permissionId;

}
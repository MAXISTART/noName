package com.school.store.admin.permission.service;


import com.school.store.admin.permission.entity.UserToPermission;
import com.school.store.base.repository.IBaseRepository;

import java.io.Serializable;
import java.util.List;

public interface UserToPermissionService extends IBaseRepository<UserToPermission, Serializable>{

    public List<UserToPermission> findByPermissionId(String permissionId);
    public List<UserToPermission> findByUserId(String userId);
    public UserToPermission findByPermissionIdAndUserId(String permissionId, String userId);
}

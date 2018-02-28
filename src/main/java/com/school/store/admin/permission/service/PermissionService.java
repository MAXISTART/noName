package com.school.store.admin.permission.service;
import com.school.store.admin.permission.entity.Permission;
import com.school.store.base.repository.IBaseRepository;

import java.io.Serializable;

public interface PermissionService extends IBaseRepository<Permission, Serializable>{
    public Permission findById(String id);
    public Permission findByName(String name);
}
package com.school.store.admin.admin.service;

import com.school.store.admin.admin.entity.Admin;
import com.school.store.base.repository.IBaseRepository;

import java.io.Serializable;
import java.util.List;

public interface AdminService extends IBaseRepository<Admin,Serializable> {

    public List<Admin> findAll();
    public Admin findById(String id);
}

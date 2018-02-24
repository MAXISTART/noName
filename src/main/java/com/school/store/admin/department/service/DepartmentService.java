package com.school.store.admin.department.service;

import com.school.store.admin.department.entity.Department;
import com.school.store.base.repository.IBaseRepository;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;
import java.util.List;

@Qualifier("DepartmentService")
public interface DepartmentService extends IBaseRepository<Department,Serializable> {

    public List<Department> findAll();
    public Department findById(String id);
    
}

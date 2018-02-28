package com.school.store.admin.user.service;

import com.school.store.admin.user.entity.User;
import com.school.store.base.repository.IBaseRepository;
import org.hibernate.annotations.Cache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

public interface UserService extends IBaseRepository<User, Serializable> {


    public Page<User> findAll(Pageable pageable);

    public User findById(String id);

    public List<User> findByDepartmentId(String departmentId);

    public User findByNameAndPassword(String name, String password);

    public User findByPhoneNumberAndPassword(String phoneNumber, String password);

    public User findByMailboxAndPassword(String mail, String password);

}

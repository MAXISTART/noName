package com.school.store.utils;

import com.school.store.admin.user.entity.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class HttpUtil {


    public static User getSessionUser(){
        // 获取 request 对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return (User)request.getSession().getAttribute("user");
    }
}

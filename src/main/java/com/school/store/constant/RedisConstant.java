package com.school.store.constant;


public interface RedisConstant {

    String TOKEN_PREFIX = "token_%s";

    String SESSIONID_PREFIX = "sessionId_%s";

    Integer EXPIRE = 7200; //2小时

    String PERMIT_ON = "ON"; //权限拦截开

    String PERMIT_OFF = "OFF"; //权限拦截关

}

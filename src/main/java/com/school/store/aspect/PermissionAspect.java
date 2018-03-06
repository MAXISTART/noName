package com.school.store.aspect;

import com.school.store.admin.cache.CacheUtil;
import com.school.store.admin.permission.entity.Permission;
import com.school.store.admin.refine.EntityRefineService;
import com.school.store.admin.user.entity.User;
import com.school.store.admin.user.service.UserService;
import com.school.store.annotation.Permiss;
import com.school.store.constant.CookieConstant;
import com.school.store.constant.RedisConstant;
import com.school.store.enums.ResultEnum;
import com.school.store.exception.BaseException;
import com.school.store.utils.CookieUtil;
import com.school.store.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class PermissionAspect {


    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    private UserService userService;

    @Autowired
    private EntityRefineService entityRefineService;

    @Autowired
    private CacheUtil permissionUtil;


    // admin文件夹下的所有文件夹下的所有controller都会受到切点影响
    @Pointcut("execution(public * com.school.store.admin.*.controller.*.*(..))")
    public  void annotationPointCut() {}



    @Before("annotationPointCut()")
    public void permissionVerify(JoinPoint joinPoint){





        log.warn("in before");

        MethodSignature sign =  (MethodSignature)joinPoint.getSignature();


        // 先获取类上面的Permission注解
        Permiss classPermission = (Permiss) sign.getDeclaringType().getAnnotation(Permiss.class);
        // 获取方法上面的permission注解
        Permiss methodPermission = sign.getMethod().getAnnotation(Permiss.class);

        // 先获取need，查看该方法是否需要权限认证
        if(methodPermission!= null && !methodPermission.need()){
            // 权限不需要，直接通过本方法
            log.warn("no need");
            return;
        }


        if(classPermission == null && methodPermission == null){
            // 如果没有注解，则表示完全无需权限认证
            return;
        }


        /**
         *  先解决注释的问题，再去解决token的验证
         */

        // 获取 cookie
        Cookie cookie = CookieUtil.get(HttpUtil.getRequest(), CookieConstant.TOKEN);
        if (cookie == null) {
            // 有异常就交给全局处理器去处理
            log.warn("【登录校验】Cookie中查不到token");
            throw new BaseException(ResultEnum.NOT_LOGIN);
        }


        // 去redis里查询
        String tokenValue = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));
        if (StringUtils.isEmpty(tokenValue)) {
            log.warn("【登录校验】Redis中查不到token");
            throw new BaseException(ResultEnum.NOT_LOGIN);
        }



        // 解析token中的userId出来
        String userId = tokenValue;

        // 设置当前的userId放进session，供controller调用
        HttpUtil.getSession().setAttribute("userId", userId);


        /**
         *  用redis来缓存权限开启功能，为每一个session都配置各自的权限开关，同时，定时清理，比如 判断当前时间是凌晨四点了，就删除这些数据。
         */
        String sessionId = HttpUtil.getSessionId();
        String turn = redisTemplate.opsForValue().get(String.format(RedisConstant.SESSIONID_PREFIX,sessionId));

        if(turn == null){
            // 如果这个session是新的，则给他配置权限开关，默认权限拦截是开启的
            turn = RedisConstant.PERMIT_ON;
            Integer expire = RedisConstant.EXPIRE;
            redisTemplate.opsForValue().set(String.format(RedisConstant.SESSIONID_PREFIX,sessionId), turn, expire, TimeUnit.SECONDS);
        }

        if(turn.equals(RedisConstant.PERMIT_OFF)){
            // 权限未开启，直接通过本方法
            return;
        }



        // 所有的permission获取完后就要进行分类了, and分一堆，or的分一堆，表达式的分一堆
        Set<String> ands = new HashSet<>();
        Set<String> ors = new HashSet<>();
        Set<String> newAnds = new HashSet<>();
        Set<String> newOrs = new HashSet<>();

        sortPermission(classPermission, ands, ors, newAnds, newOrs);
        sortPermission(methodPermission, ands, ors, newAnds, newOrs);



        // 先根据userId从缓存或者数据库中得到user的权限
        User user = userService.findById(userId);
        if(user == null){
            throw new BaseException(ResultEnum.USER_NOT_FOUND);
        }
        Set<Permission> permissions = permissionUtil.getUserPermissionByIdWithCache(user.getId());

        log.warn("ands : " + ands);
        log.warn("ors : " + ors);

        // 检测权限
        for(String and : ands){
            if(hasPermission(permissions, and)){
                // 如果有该权限就继续验证
                continue;
            }else{
                // 如果没有，则权限不足
                throw new BaseException(ResultEnum.PERMISSION_NOT_ALLOWED);
            }
        }

        log.warn("and pass");

        boolean isCorrect = false;
        for(String or : ors){
            if(hasPermission(permissions, or)){
                // 如果有该权限就跳出循环
                isCorrect = true;
                break;
            }
        }

        log.warn("or pass");

        // 如果循环结束后还是没有，则权限不足
        if(!isCorrect && ors.size()>0){
            throw new BaseException(ResultEnum.PERMISSION_NOT_ALLOWED);
        }

        // 权限通过，可以继续执行

    }






    /**
     *  关闭权限登录功能，供controller以及系统调用
     */
    public void closePermit(){
        String sessionId = HttpUtil.getSessionId();
        redisTemplate.opsForValue().set(String.format(RedisConstant.SESSIONID_PREFIX,sessionId), RedisConstant.PERMIT_OFF);
        log.warn("权限认证已经关闭");
    }
    /**
     *  开启权限登录功能，供controller以及系统调用
     */
    public void activePermit(){
        String sessionId = HttpUtil.getSessionId();
        redisTemplate.opsForValue().set(String.format(RedisConstant.SESSIONID_PREFIX,sessionId), RedisConstant.PERMIT_ON);
        log.warn("权限认证已经开启");
    }






    public void sortPermission(Permiss permission, Set<String> ands, Set<String> ors, Set<String> newAnds, Set<String> newOrs){
        if(permission == null){
            return;
        }
        if(permission.and().length > 0){
            putInSet(permission.and(), ands);
        }
        if(permission.or().length > 0){
            putInSet(permission.or(), ors);
        }
        if(permission.newAnd().length > 0){
            putInSet(permission.newAnd(), newAnds);
        }
        if(permission.newOr().length > 0){
            putInSet(permission.newOr(), newOrs);
        }

        if(newAnds.size() > 0 || newOrs.size() > 0){
            // 覆盖原来的and
            ands.clear();
            Union(ands, newAnds);
            // 覆盖原来的or
            ors.clear();
            Union(ors, newOrs);
        }
    }



    public void putInSet(String[] data, Set<String> temp){
        for(String data_el : data){
            temp.add(data_el);
        }
    }


    public boolean hasPermission(Set<Permission> permissions, String permissionName){
        if(permissions == null){
            return false;
        }
        for(Permission permission : permissions){
            if(permission.getName().equals(permissionName)){
                return true;
            }
        }
        return false;
    }

    /*
     * union方法计算并集
     */
    public Set<String> Union(Set<String> s1 , Set<String> s2){

        HashSet<String> unionSet = new HashSet<String>();
        for(String el : s2){
            s1.add(el);
        }
        return unionSet ;
    }

}

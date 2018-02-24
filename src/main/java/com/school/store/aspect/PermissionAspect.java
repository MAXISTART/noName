package com.school.store.aspect;

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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

@Aspect
@Component
@Slf4j
public class PermissionAspect {

    private boolean permissionActive = true;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    private UserService userService;

    @Autowired
    private EntityRefineService entityRefineService;


    // admin文件夹下的所有文件夹下的所有controller都会受到切点影响
    @Pointcut("execution(public * com.school.store.admin.*.controller.*.*(..))")
    public  void annotationPointCut() {}



    @Before("annotationPointCut()")
    public void permissionVerify(JoinPoint joinPoint){

        log.warn("permissionActive is " + permissionActive);


        if(!permissionActive){
            // 权限未开启，直接结束
            return;
        }

        // 获取 request 对象,测试用的
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();




        log.warn("in before");

        MethodSignature sign =  (MethodSignature)joinPoint.getSignature();
        // 先获取类上面的Permission注解
        Permiss[] classPermissions = (Permiss[]) sign.getDeclaringType().getAnnotationsByType(Permiss.class);
        // 获取方法上面的permission注解
        Permiss[] methodPermissions = sign.getMethod().getAnnotationsByType(Permiss.class);


        if(classPermissions.length == 0 && methodPermissions.length == 0){
            // 如果没有，则权限不足
            throw new BaseException(ResultEnum.PERMISSION_NOT_ALLOWED);
        }

        // 所有的permission获取完后就要进行分类了, and分一堆，or的分一堆，表达式的分一堆
        Set<String> ands = new HashSet<>();
        Set<String> ors = new HashSet<>();
        Set<String> newAnds = new HashSet<>();
        Set<String> newOrs = new HashSet<>();

        sortPermission(classPermissions, ands, ors, newAnds, newOrs);
        sortPermission(methodPermissions, ands, ors, newAnds, newOrs);


/*        // 获取 cookie
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie == null) {
            // 有异常就交给全局处理器去处理
            log.warn("【登录校验】Cookie中查不到token");
            //throw new BaseException(ResultEnum.PERMISSION_NOT_ALLOWED);
        }


        // 去redis里查询
        String tokenValue = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));
        if (StringUtils.isEmpty(tokenValue)) {
            log.warn("【登录校验】Redis中查不到token");
            //throw new BaseException(ResultEnum.PERMISSION_NOT_ALLOWED);
        }*/

        // 解析token中的userId出来
        String userId = "1";

        // 先根据userId从数据库中得到user的权限
        User user = userService.findById(userId);
        entityRefineService.refine(user);
        Set<Permission> permissions = user.getPermissions();

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


    public void sortPermission(Permiss[] permissions, Set<String> ands, Set<String> ors, Set<String> newAnds, Set<String> newOrs){

        for(Permiss permission : permissions){
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
        }

        if(newAnds.size() > 0){
            // 覆盖原来的and
            ands.clear();
            Union(ands, newAnds);
        }

        if(newOrs.size() > 0){
            // 覆盖原来的and
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

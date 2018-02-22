package com.school.store.aspect;

import com.school.store.annotation.Permission;
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

    @Autowired
    private StringRedisTemplate redisTemplate;

    // admin文件夹下的所有文件夹下的所有controller都会受到切点影响
    @Pointcut("execution(public * com.school.store.admin.*.controller.*.*(..))")
    public  void annotationPointCut() {}



    @Before("annotationPointCut()")
    public void permissionVerify(JoinPoint joinPoint){
        System.out.println("in before");
        MethodSignature sign =  (MethodSignature)joinPoint.getSignature();
        // 先获取类上面的Permission注解
        Permission[] classPermissions = (Permission[]) sign.getDeclaringType().getAnnotationsByType(Permission.class);
        // 获取方法上面的permission注解
        Permission[] methodPermissions = sign.getMethod().getAnnotationsByType(Permission.class);

        // 所有的permission获取完后就要进行分类了, and分一堆，or的分一堆，表达式的分一堆
        Set<String> ands = new HashSet<>();
        Set<String> ors = new HashSet<>();
        Set<String> expressions = new HashSet<>();

        sortPermission(classPermissions, ands, ors, expressions);
        sortPermission(methodPermissions, ands, ors, expressions);

        // 获取 request 对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 获取 cookie
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie == null) {
            // 有异常就交给全局处理器去处理
            log.warn("【登录校验】Cookie中查不到token");
            throw new BaseException(ResultEnum.PERMISSION_NOT_ALLOWED);
        }


        // 去redis里查询
        String tokenValue = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));
        if (StringUtils.isEmpty(tokenValue)) {
            log.warn("【登录校验】Redis中查不到token");
            throw new BaseException(ResultEnum.PERMISSION_NOT_ALLOWED);
        }

        // 检测权限

    }


    public void sortPermission(Permission[] permissions, Set<String> ands, Set<String> ors, Set<String> expressions){
        for(Permission permission : permissions){
            if(permission.and() != null){
                ands.add(permission.and());
            }
            if(permission.or() != null){
                ors.add(permission.or());
            }
            if(permission.expression() != null){
                expressions.add(permission.expression());
            }
        }
    }

}

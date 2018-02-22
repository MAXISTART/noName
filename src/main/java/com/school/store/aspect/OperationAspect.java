package com.school.store.aspect;

import com.school.store.annotation.MyLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class  OperationAspect {

    @Pointcut("@annotation(com.school.store.annotation.MyLog)")
    public  void annotationPointCut() {}

    @Before("annotationPointCut()")
    public void before(JoinPoint joinPoint){
        System.out.println("in before");
        MethodSignature sign =  (MethodSignature)joinPoint.getSignature();
        MyLog myLog = (MyLog) sign.getDeclaringType().getAnnotation(MyLog.class);
        if(myLog != null){
            System.out.println(myLog.value());
        }else{
            System.out.println("myLog empty");
        }
        Method method = sign.getMethod();
        MyLog annotation = method.getAnnotation(MyLog.class);
        System.out.print("打印："+annotation.value()+" 前置日志");
    }
}


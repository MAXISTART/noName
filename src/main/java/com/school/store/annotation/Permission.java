package com.school.store.annotation;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permission {


    // 这个属性用在 方法或者类 上，表示一定需要的permission
    String and();

    // 这个属性用在 方法或者类 上，表示或者需要的permission，在or的所有里面一定要选一个
    String or();

    // 这个属性用在 方法或者类 上，表示覆盖之前的所有注解，用表达式的方式来确定注解
    String expression();
}

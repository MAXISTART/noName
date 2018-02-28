package com.school.store.annotation;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permiss {


    // 这个属性用在 方法或者类 上，表示一定需要的permission
    String[] and() default {};

    // 这个属性用在 方法或者类 上，表示或者需要的permission，在or的所有里面一定要选一个
    String[] or() default {};

    // 这个属性用在 方法或者类 上，表示覆盖之前的and()
    String[] newAnd() default {};

    String[] newOr() default {};

    // 这个属性表示这个方法是否 需要权限认证，默认是需要的
    boolean need() default true;
}

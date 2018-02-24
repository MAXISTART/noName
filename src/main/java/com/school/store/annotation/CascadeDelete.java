package com.school.store.annotation;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CascadeDeletes.class)
public @interface CascadeDelete {
    // 需要级联删除的类
    Class value();
    // 级联的条件
    String filter();
    // 级联的参数
    String[] args() default {};
}

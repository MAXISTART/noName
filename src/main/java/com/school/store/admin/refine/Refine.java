package com.school.store.admin.refine;


import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented

public @interface Refine {

    RefineMethod value();
    // 提供九个外键去引用，比如，argName_0的值存放的就是method[0]的所有需要用到的属性的名称
    String[] argNames() default {};
}


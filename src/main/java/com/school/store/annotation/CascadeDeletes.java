package com.school.store.annotation;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CascadeDeletes {
    CascadeDelete[] value();
}

package com.heaboy.annotation;

import java.lang.annotation.*;

/**
 * @author 章霆
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface RequestMapping {
    String value() default "";
}

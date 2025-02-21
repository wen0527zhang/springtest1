package com.neworange.gb28181.utils;

import java.lang.annotation.*;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/10 15:59
 * @description
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageElement {
    String value();

    String subVal() default "";
}

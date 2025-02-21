package com.neworange.utils.files.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/6/23 21:37
 * @ description
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Component
@Inherited
public @interface UploadMode {
    UploadModeEnum mode();
}

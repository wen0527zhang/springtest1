package com.neworange.utils.files.annotation;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/6/23 21:38
 * @ description
 */
@AllArgsConstructor
@Getter
public enum  UploadModeEnum {
    RANDOM_ACCESS,MAPPED_BYTEBUFFER
}

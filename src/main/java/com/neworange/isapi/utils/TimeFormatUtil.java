package com.neworange.isapi.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author zhengxiaohui
 * @date 2024/1/18 11:54
 * @desc
 */
public class TimeFormatUtil {

    public static String curTimeFormat() {
        // 创建 DateTimeFormatter 对象，用于定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

        // 获取当前时间并格式化为字符串
        return LocalDateTime.now().format(formatter);
    }
}

package com.neworange.isapi.demo.entity;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhengxiaohui
 * @date 2024/1/10 10:57
 * @desc
 */
public class DeviceLinkStatusDTO {

    /**
     * 是否停止链接
     */
    public AtomicBoolean stopLink = new AtomicBoolean(false);
    /**
     * 是否接收到数据
     */
    public AtomicBoolean dataRecv = new AtomicBoolean(false);
}

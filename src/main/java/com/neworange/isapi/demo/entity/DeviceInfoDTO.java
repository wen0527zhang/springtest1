package com.neworange.isapi.demo.entity;

import lombok.Data;

/**
 * @author zhengxiaohui
 * @date 2024/1/9 16:37
 * @desc 设备信息字段
 */
@Data
public class DeviceInfoDTO {

    /**
     * 设备ip地址
     *
     */
    public String devIp;
    /**
     * 设备端口
     */
    public String devPort;

    /***
     * 设备用户名
     */
    public String username;

    /**
     * 设备登录密码
     */
    public String password;

    /**
     * 使用的调用方式
     * 0: http （默认）
     * 1: https
     */
    public int httpType;
}

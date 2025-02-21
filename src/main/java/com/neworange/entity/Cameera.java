package com.neworange.entity;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/9 17:24
 * @description
 */

@Data
public class Cameera  {
    /**
     * 区域编码
     */
    private String Countyid="6103291";
    /**
     * 端口号
     */
    private int DevPort=8000;
    /**
     * 设置id
     */
    private String DeviceID;
    /**
     * sdk 类型
     */
    private int HistSdkType=1;
    /**
     * ip地址
     */
    @ExcelProperty(value = "IP地址",index = 1)
    private String IPAddress;
    /**
     *经度
     */
    private double Latitude=0.0;
    /**
     *纬度
     */
    private double Longitude=0.0;
    /**
     * 类型
     */
    private String Model="hik";
    /**
     * 名称
     */
    @ExcelProperty(value = "设备名称",index = 0)
    private String Name;
    /**
     * 父节点
     */

    private String ParentID="61032900002000000122";

    /**
     * 平台国标id
     */
    private String PlatGbId="61032900002000000122";
    /**
     * 实时sdk类型
     */
    private int RealSdkType = 1;
    /**
     * 状态
     */
    private boolean Status = true;
    /**
     * 用户名
     */
    @ExcelProperty(value = "帐号", index = 2)
    private String Username = "admin";
    /**
     * 密码
     */
    @ExcelProperty(value = "密码", index = 3)
    private String Password;
    /**
     * 厂商
     */
    private String Vender = "hik";
}

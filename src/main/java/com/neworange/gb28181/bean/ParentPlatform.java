package com.neworange.gb28181.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/10 19:04
 * @description
 */
@Data
@Schema(description = "平台信息")
public class ParentPlatform {
    /**
     * id
     */
    @Schema(description = "ID(数据库中)")
    private Integer id;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private boolean enable;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * SIP服务国标编码
     */
    @Schema(description = "SIP服务国标编码")
    private String serverGBId;

    /**
     * SIP服务国标域
     */
    @Schema(description = "SIP服务国标域")
    private String serverGBDomain;

    /**
     * SIP服务IP
     */
    @Schema(description = "SIP服务IP")
    private String serverIP;

    /**
     * SIP服务端口
     */
    @Schema(description = "SIP服务端口")
    private int serverPort;

    /**
     * 设备国标编号
     */
    @Schema(description = "设备国标编号")
    private String deviceGBId;

    /**
     * 设备ip
     */
    @Schema(description = "设备ip")
    private String deviceIp;

    /**
     * 设备端口
     */
    @Schema(description = "设备端口")
    private int devicePort;

    /**
     * SIP认证用户名(默认使用设备国标编号)
     */
    @Schema(description = "SIP认证用户名(默认使用设备国标编号)")
    private String username;

    /**
     * SIP认证密码
     */
    @Schema(description = "SIP认证密码")
    private String password;

    /**
     * 注册周期 (秒)
     */
    @Schema(description = "注册周期 (秒)")
    private int expires;

    /**
     * 心跳周期(秒)
     */
    @Schema(description = "心跳周期(秒)")
    private int keepTimeout;

    /**
     * 传输协议
     * UDP/TCP
     */
    @Schema(description = "传输协议")
    private String transport;

    /**
     * 字符集
     */
    @Schema(description = "字符集")
    private String characterSet;

    /**
     * 允许云台控制
     */
    @Schema(description = "允许云台控制")
    private boolean ptz;

    /**
     * RTCP流保活
     */
    @Schema(description = "RTCP流保活")
    private boolean rtcp;

    /**
     * 在线状态
     */
    @Schema(description = "在线状态")
    private boolean status;

    /**
     * 在线状态
     */
    @Schema(description = "在线状态")
    private int channelCount;

    /**
     * 默认目录Id,自动添加的通道多放在这个目录下
     */
    @Schema(description = "默认目录Id,自动添加的通道多放在这个目录下")
    private String catalogId;

    /**
     * 已被订阅目录信息
     */
    @Schema(description = "已被订阅目录信息")
    private boolean catalogSubscribe;

    /**
     * 已被订阅报警信息
     */
    @Schema(description = "已被订阅报警信息")
    private boolean alarmSubscribe;

    /**
     * 已被订阅移动位置信息
     */
    @Schema(description = "已被订阅移动位置信息")
    private boolean mobilePositionSubscribe;

    /**
     * 点播未推流的设备时是否使用redis通知拉起
     */
    @Schema(description = "点播未推流的设备时是否使用redis通知拉起")
    private boolean startOfflinePush;

    /**
     * 目录分组-每次向上级发送通道信息时单个包携带的通道数量，取值1,2,4,8
     */
    @Schema(description = "目录分组-每次向上级发送通道信息时单个包携带的通道数量，取值1,2,4,8")
    private int catalogGroup;

    /**
     * 行政区划
     */
    @Schema(description = "行政区划")
    private String administrativeDivision;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private String updateTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "是否作为消息通道")
    private boolean asMessageChannel;

    @Schema(description = "是否作为消息通道")
    private boolean autoPushChannel;

    @Schema(description = "点播回复200OK使用次IP")
    private String sendStreamIp;
}

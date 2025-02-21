package com.neworange.isapi.demo.function.basic;


import com.neworange.isapi.demo.entity.DeviceInfoDTO;
import com.neworange.isapi.demo.entity.DeviceLinkStatusDTO;
import com.neworange.isapi.demo.function.basic.guard.LongLinkThread;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhengxiaohui
 * @date 2023/12/22 17:36
 * @desc ISAPI布防示例demo代码
 */
@Slf4j
public class AlarmGuardDemo {

    /**
     * 管理设备的布防状态情况
     */
    public final static ConcurrentHashMap<DeviceInfoDTO, DeviceLinkStatusDTO> devLinkStatusMap = new ConcurrentHashMap<>();

    /**
     * 开始布防
     */
    public void startGuard(DeviceInfoDTO deviceInfo) {
        this.stopGuard(deviceInfo);

        // 添加设备布防状态
        DeviceLinkStatusDTO status = new DeviceLinkStatusDTO();
        status.stopLink = new AtomicBoolean(false);
        status.dataRecv = new AtomicBoolean(false);
        AlarmGuardDemo.devLinkStatusMap.put(deviceInfo, status);

        Thread thread = new Thread(new LongLinkThread(deviceInfo));
        thread.start();
    }

    /**
     * 停止布防
     */
    public void stopGuard(DeviceInfoDTO deviceInfo) {
        DeviceLinkStatusDTO devLongLinkStatus = AlarmGuardDemo.devLinkStatusMap.get(deviceInfo);
        if (devLongLinkStatus == null) {
            return;
        }
        devLongLinkStatus.stopLink.set(true);
        devLongLinkStatus.dataRecv.set(false);
        AlarmGuardDemo.devLinkStatusMap.put(deviceInfo, devLongLinkStatus);
    }


    /**
     * 开始订阅
     */
    public void startSubscribe(DeviceInfoDTO deviceInfo) {
        this.stopSubscribe(deviceInfo);

        // 添加设备布防状态
        DeviceLinkStatusDTO status = new DeviceLinkStatusDTO();
        status.stopLink = new AtomicBoolean(false);
        status.dataRecv = new AtomicBoolean(false);
        AlarmGuardDemo.devLinkStatusMap.put(deviceInfo, status);

        Thread thread = new Thread(new LongLinkThread(deviceInfo, true, "IDCardInfoEvent"));
        thread.start();
    }

    /**
     * 停止订阅
     */
    public void stopSubscribe(DeviceInfoDTO deviceInfo) {
        DeviceLinkStatusDTO devLongLinkStatus = AlarmGuardDemo.devLinkStatusMap.get(deviceInfo);
        if (devLongLinkStatus == null) {
            return;
        }
        devLongLinkStatus.stopLink.set(true);
        devLongLinkStatus.dataRecv.set(false);
        AlarmGuardDemo.devLinkStatusMap.put(deviceInfo, devLongLinkStatus);
    }

}

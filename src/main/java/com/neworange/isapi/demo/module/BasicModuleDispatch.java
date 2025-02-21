package com.neworange.isapi.demo.module;


import com.neworange.isapi.demo.entity.DeviceInfoDTO;
import com.neworange.isapi.demo.function.basic.AlarmGuardDemo;
import com.neworange.isapi.demo.function.basic.AlarmListenDemo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengxiaohui
 * @date 2023/12/22 18:47
 * @desc 基础功能模块的路由
 */
@Slf4j
public class BasicModuleDispatch {
    private AlarmGuardDemo alarmGuardDemo = new AlarmGuardDemo();
    private AlarmListenDemo alarmListenDemo = new AlarmListenDemo();

    public void dispatch(DeviceInfoDTO deviceInfo, String command) {
        switch (command) {
            case "f0001": {
                log.info("\n[Function]开启" + deviceInfo.getDevIp() + "_" + deviceInfo.getDevPort() + "ISAPI布防");
                alarmGuardDemo.startGuard(deviceInfo);
                break;
            }
            case "f0002": {
                log.info("\n[Function]关闭" + deviceInfo.getDevIp() + "_" + deviceInfo.getDevPort() + "ISAPI布防");
                alarmGuardDemo.stopGuard(deviceInfo);
                break;
            }
            case "f0003": {
                log.info("\n[Function]开启" + deviceInfo.getDevIp() + "_" + deviceInfo.getDevPort() + "ISAPI订阅");
                alarmGuardDemo.startSubscribe(deviceInfo);
                break;
            }
            case "f0004": {
                log.info("\n[Function]关闭" + deviceInfo.getDevIp() + "_" + deviceInfo.getDevPort() + "ISAPI订阅");
                alarmGuardDemo.stopSubscribe(deviceInfo);
                break;
            }

            case "f0031": {
                log.info("\n[Function]开启监听");
                alarmListenDemo.startListen();
                break;
            }
            case "f0032": {
                log.info("\n[Function]关闭监听");
                alarmListenDemo.stopListen();
                break;
            }
        }
    }

}

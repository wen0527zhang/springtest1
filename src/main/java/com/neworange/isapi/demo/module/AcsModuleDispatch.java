package com.neworange.isapi.demo.module;

import com.neworange.isapi.demo.entity.DeviceInfoDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengxiaohui
 * @date 2024/1/9 16:53
 * @desc 门禁相关demo功能点路由
 */
@Slf4j
public class AcsModuleDispatch {

    public void dispatch(DeviceInfoDTO deviceInfo, String command) {
        switch (command) {
            case "10001": {
                log.info("\n[Function]门禁功能点1");
                break;
            }
            case "10002": {
                log.info("\n[Function]门禁功能点2");
                break;
            }
        }
    }
}

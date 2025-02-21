package com.neworange.isapi.demo.module;


import com.neworange.isapi.demo.entity.DeviceInfoDTO;
import com.neworange.isapi.demo.function.audio.AudioManage;
import com.neworange.isapi.demo.function.audio.AudioPlanManage;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengxiaohui
 * @date 2024/1/12 14:11
 * @desc 音柱相关demo功能点路由
 */
@Slf4j
public class AudioModuleDispatch {

    public void dispatch(DeviceInfoDTO deviceInfo, String command) throws Exception {
        switch (command) {
            case "40001": {
                log.info("\n[Function]TTS语音功能");
                AudioManage.ttsAudioCfg(deviceInfo);
                break;
            }
            case "40002": {
                log.info("\n[Function]上传自定义音频文件到设备");
                AudioManage.uploadCustomAudioFile(deviceInfo);
                break;
            }
            case "40003": {
                log.info("\n[Function]获取设备已有的音频文件信息");
                AudioManage.getAudioInfo(deviceInfo);
                break;
            }
            case "40004": {
                log.info("\n[Function]删除单个自定义音频文件");
                AudioManage.deleteCustomAudioFile(deviceInfo);
                break;
            }
            case "40005": {
                log.info("\n[Function]播放单个自定义音频文件");
                AudioManage.playCustomAudioFile(deviceInfo);
                break;
            }
            case "40006": {
                log.info("\n[Function]停止播放单个自定义音频文件");
                AudioManage.stopPlayCustomAudioFile(deviceInfo);
                break;
            }
            case "40007": {
                log.info("\n[Function]批量创建广播计划方案");
                AudioPlanManage.addPlanScheme(deviceInfo);
                break;
            }
            case "40008": {
                log.info("\n[Function]查询全量广播计划");
                AudioPlanManage.searchPlanScheme(deviceInfo);
                break;
            }
            default: {
                log.info("\n[Function]未实现的功能demo示例点");
            }
        }
    }
}

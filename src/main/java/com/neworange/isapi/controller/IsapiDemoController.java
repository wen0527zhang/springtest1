package com.neworange.isapi.controller;


import com.alibaba.fastjson2.JSONObject;
import com.neworange.isapi.demo.entity.DeviceInfoDTO;
import com.neworange.isapi.demo.module.AcsModuleDispatch;
import com.neworange.isapi.demo.module.AudioModuleDispatch;
import com.neworange.isapi.demo.module.BasicModuleDispatch;
import com.neworange.isapi.utils.HTTPClientUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhengxiaohui
 * @date 2023/12/22 18:52
 * @desc
 */
@RestController
public class IsapiDemoController {
    private BasicModuleDispatch basicModuleDispatch = new BasicModuleDispatch();
    private AudioModuleDispatch audioModuleDispatch = new AudioModuleDispatch();
    private AcsModuleDispatch acsModuleDispatch = new AcsModuleDispatch();


    @GetMapping(value = "/testAudio")
    public String testDev(String command) throws Exception {
        DeviceInfoDTO deviceInfo = new DeviceInfoDTO();
        deviceInfo.setDevIp("172.16.100.11");
        deviceInfo.setDevPort("80");
        deviceInfo.setUsername("admin");
        deviceInfo.setPassword("xincheng191213");
        return runDemo(command, deviceInfo);
    }
    @GetMapping(value = "/playCustomAudioFile")
    public String playCustomAudioFile(String command) throws Exception {
        DeviceInfoDTO deviceInfo = new DeviceInfoDTO();
        deviceInfo.setDevIp("172.16.100.11");
        deviceInfo.setDevPort("80");
        deviceInfo.setUsername("admin");
        deviceInfo.setPassword("xincheng191213");
        JSONObject parameter = new JSONObject();
        parameter.put("audioOutID",new int[]{1});//audioOutID: 进行音频播放的音频输出口(通道)ID
        parameter.put("audioVolume",50);
        parameter.put("audioLevel",0);
        parameter.put("playDurationMode","auto");

         String s = HTTPClientUtil.doPut(deviceInfo, "/ISAPI/AccessControl/EventCardLinkageCfg/CustomAudio/" + command + "/play?format=json", parameter.toJSONString());
        return s;
    }

    @GetMapping(value = "/ttsAudioCfg")
    public String ttsAudioCfg(String tts,int audioVolume,int voiceType) throws Exception {
        DeviceInfoDTO deviceInfo = new DeviceInfoDTO();
        deviceInfo.setDevIp("172.16.100.11");
        deviceInfo.setDevPort("80");
        deviceInfo.setUsername("admin");
        deviceInfo.setPassword("xincheng191213");
        JSONObject parameter = new JSONObject();
        parameter.put("command","start");
        parameter.put("TTSContent", tts);//TTS语音文本
        parameter.put("audioLevel",0);
        parameter.put("audioVolume",audioVolume);
        parameter.put("audioOutID",new int[]{1});
        if(voiceType==1){
            parameter.put("voiceType","female");
        }else {
            parameter.put("voiceType","male");
        }

        final String res = HTTPClientUtil.doPut(deviceInfo,
                "/ISAPI/AccessControl/EventCardLinkageCfg/TTSAudio?format=json", parameter.toJSONString());
        return res;
    }

    @GetMapping(value = "/runDemo")
    public String runDemo(String command, DeviceInfoDTO deviceInfo) throws Exception {
        if (command == null || "".equals(command.trim())) {
            return "请指定command";
        }

        // 这里指令前缀第一位为16进制 0~F
        switch (command.substring(0, 1)) {
            case "f": {
                /**
                 * F0001~F9999 预留【SDK通用服务】示例代码
                 */
                System.out.println("\n[Module]通用的sdk服务实例代码");
                basicModuleDispatch.dispatch(deviceInfo, command);
                break;
            }
            case "1": {
                /**
                 * 10001~19999 预留【门禁系统】相关的代码实现
                 * 门禁设备相关业务接口
                 */
                System.out.println("\n[Module]门禁系统相关的demo示例代码");
                acsModuleDispatch.dispatch(deviceInfo, command);
                break;
            }
            case "2": {
                /**
                 * 20001~29999 预留【出入口系统】相关的代码实现
                 * 出入口设备相关业务接口
                 */
                System.out.println("\n[Module]出入口系统相关的demo示例代码");
//                        DeepInMindFunctionDemo.dispatch(str, lLoginID);
                break;
            }
            case "3": {
                /**
                 * 30001~39999 预留【超脑】相关的代码实现
                 * 超脑设备相关业务接口
                 */
//                System.out.println("\n[Module]超脑系统相关的demo示例代码");
//                        DeepInMindFunctionDemo.dispatch(str, lLoginID);
                break;
            }
            case "4": {
                /**
                 * 40001~49999 预留【音柱】相关的代码实现
                 * 音频广播设备相关业务接口
                 */
                System.out.println("\n[Module]音频广播相关的demo示例代码");
                audioModuleDispatch.dispatch(deviceInfo, command);
                break;
            }
            default: {
                System.out.println("\n未知的指令操作!请重新输入!\n");
            }
        }
        return command;
    }




}

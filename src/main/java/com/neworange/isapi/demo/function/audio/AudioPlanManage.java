package com.neworange.isapi.demo.function.audio;


import com.neworange.isapi.demo.entity.DeviceInfoDTO;
import com.neworange.isapi.utils.ConfigFileUtil;
import com.neworange.isapi.utils.HTTPClientUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhengxiaohui
 * @date 2024/1/12 14:27
 * @desc 广播计划管理
 */
public class AudioPlanManage {

    /**
     * 批量创建广播计划方案
     */
    public static void addPlanScheme(DeviceInfoDTO deviceInfoDTO) {

        Map<String, Object> parameter = new HashMap<>();
        //广播计划方案1信息
        parameter.put("planSchemeID1","定时广播任务1");//计划方案名称
        parameter.put("startTime1","2023-11-22");//计划的开始日期
        parameter.put("stopTime1","2024-01-15");//计划的结束日期
        parameter.put("beginTime1","10:00:00 08:00");//当天的时间段起始点
        parameter.put("endTime1","18:00:00 08:00");//当天的时间段结束点
        parameter.put("playMode1","loop");//播放模式[order#顺序播放一遍, loop#指定时间内循环播放]

        //广播计划方案2信息
        parameter.put("planSchemeID2","定时广播任务2");//计划方案名称
        parameter.put("startTime2","2023-11-22");//计划的开始日期
        parameter.put("stopTime2","2024-01-15");//计划的结束日期
        parameter.put("beginTime2","10:00:00 08:00");//当天的时间段起始点
        parameter.put("endTime2","18:00:00 08:00");//当天的时间段结束点
        parameter.put("playMode2","order");//播放模式[order#顺序播放一遍, loop#指定时间内循环播放]

        //广播操作信息2
        parameter.put("speechSynthesisContent2","定时广播计划2");//语音合成内容

        String input= ConfigFileUtil.getReqBodyFromTemplate("isapi/audio/addPlanScheme.json", parameter);

        HTTPClientUtil.doPost(deviceInfoDTO,
                "/ISAPI/VideoIntercom/broadcast/AddPlanScheme?format=json",input);

    }

    /**
     * 查询全量广播计划
     */
    public static void searchPlanScheme(DeviceInfoDTO deviceInfoDTO) {

        Map<String, Object> parameter = new HashMap<>();
        String input=ConfigFileUtil.getReqBodyFromTemplate("isapi/audio/searchPlanScheme.json", parameter);
        System.out.println(input);
        HTTPClientUtil.doPost(deviceInfoDTO,
                "/ISAPI/VideoIntercom/broadcast/planSearch?format=json",input);
    }
}

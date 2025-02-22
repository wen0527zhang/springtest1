package com.neworange.utils;

import ai.onnxruntime.OrtException;
import com.alibaba.fastjson2.JSONArray;
import com.neworange.detect.Yolov8sOnnxRuntimeDetect;
import com.neworange.entity.Detection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * Yolov8
 * 物体检测
 *
 * @author gc.x
 */
@Slf4j
public class Yolo8Util {
    public static List<Detection> runOcr(String imagePath) throws OrtException, IOException {
        long start_time = System.currentTimeMillis();
        Yolov8sOnnxRuntimeDetect criteria = Yolov8sOnnxRuntimeDetect.criteria();
        List<Detection> detections = criteria.runOcr(imagePath);
        log.info("time：%d ms.", (System.currentTimeMillis() - start_time));
        log.info(JSONArray.toJSONString(detections));
        return detections;
    }
}

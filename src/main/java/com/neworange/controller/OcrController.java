package com.neworange.controller;


import com.neworange.entity.Detection;
import com.neworange.entity.OcrParam;
import com.neworange.entity.WordBlock;
import com.neworange.ocrlib.TextBlock;
import com.neworange.service.BizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author gc.x
 * @date 2023/11/6
 **/
@Slf4j
@RestController
@RequestMapping
@Tag(name = "OCR应用")
public class OcrController {

    @Autowired
    private BizService bizService;
    @PostMapping("/ocr")
    @Operation(summary = "识别文字(RapidOcr)")
    public String ocr(OcrParam param, MultipartFile file, HttpServletRequest request) throws Exception {
        return bizService.ocr(param,file,request);
    }
    @PostMapping("/ocr/json")
    @Operation(summary = "识别文字(RapidOcr)")
    public List<TextBlock> jsonOcr(MultipartFile file, HttpServletRequest request) throws Exception {
        return bizService.jsonOcr(null,file,request);
    }
    @PostMapping("/word")
    @Operation(summary = "识别文字(DJL)")
    public String ppWorld(MultipartFile file, HttpServletRequest request) throws Exception {
        return bizService.ppWorld(null,file,request);
    }
    @PostMapping("/word/json")
    @Operation(summary = "识别文字(DJL)")
    public List<WordBlock> jsonPpWorld(MultipartFile file, HttpServletRequest request) throws Exception {
        return bizService.jsonPpWorld(null,file,request);
    }
    @PostMapping("/yolo8")
    @Operation(summary = "识别物体(YOLOV8)")
    public String yolo(MultipartFile file, HttpServletRequest request) throws Exception {
        return bizService.yolo(null,file,request);
    }
    @PostMapping("/yolo8/json")
    @Operation(summary = "识别物体(YOLOV8)")
    public List<Detection> jsonYolo(MultipartFile file, HttpServletRequest request) throws Exception {
        return bizService.jsonYolo(null,file,request);
    }
    @PostMapping("/fire/json")
    @Operation(summary = "火焰检测")
    public String fireYolo(MultipartFile file, HttpServletRequest request) throws Exception {
        return bizService.fireYolo(null,file,request).toJson();
    }
    @PostMapping("/reflective/json")
    @Operation(summary = "反光衣检测")
    public String reflective(MultipartFile file, HttpServletRequest request) throws Exception {
        return bizService.reflective(null,file,request).toJson();
    }
    @PostMapping("/construction/json")
    @Operation(summary = "安全帽检测")
    public String construction(MultipartFile file, HttpServletRequest request) throws Exception {
        return bizService.construction(null,file,request).toJson();
    }
}

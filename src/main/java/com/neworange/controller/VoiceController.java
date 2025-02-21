package com.neworange.controller;


import com.neworange.vosk.VoiceUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author gc.x
 * @date 2023/11/11
 **/
@Slf4j
@RestController
@RequestMapping("/voice")
@Tag(name = "语言识别应用")
public class VoiceController {

    @Autowired
    private VoiceUtil voiceUtil;

    @PostMapping("/accept")
    @Operation(summary = "识别 wav")
    public String accept(MultipartFile file, HttpServletRequest request) throws Exception {
        return voiceUtil.accept(file);
    }
}

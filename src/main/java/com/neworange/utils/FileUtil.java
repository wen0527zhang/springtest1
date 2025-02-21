package com.neworange.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Service
public class FileUtil {
    @Autowired
    private FileProperties fileProperties;
    public  String getPath(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        log.info("originalFilename:" + originalFilename);
        // 获取文件后缀
        String extension = FilenameUtils.getExtension(originalFilename);
        String newFileName = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN) + RandomStringUtils.randomNumeric(6) + "." + extension;
        log.info("newFileName:" + newFileName);
        // 本地文件上传路径
        String uploadPath = fileProperties.getUploadPath();
        File uploadDir = new File(uploadPath);
        // 上传目录不存在，则直接创建
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        // 上传文件到本地目录
        File uploadFile = new File(uploadDir, newFileName);
        log.info("uploadFile:" + uploadFile);
        file.transferTo(uploadFile);
        return uploadPath+newFileName;
    }

}

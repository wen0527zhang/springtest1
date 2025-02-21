package com.neworange.service;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import ai.onnxruntime.OrtException;
import com.alibaba.fastjson2.JSONObject;
import com.neworange.entity.FaceObject;
import com.neworange.entity.FaceParam;
import com.neworange.entity.PersonObject;
import com.neworange.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class FaceService {
    @Autowired
    private FileProperties fileProperties;

    public String addFace(FaceParam param, MultipartFile file, HttpServletRequest request) throws IOException, TranslateException, ModelNotFoundException, MalformedModelException, OrtException {
        List<FaceParam> allJson = getAllJson();

        if(!CollectionUtils.isEmpty(allJson)){
            boolean b = allJson.stream().anyMatch(v -> Objects.equals(v.getPersonId(), param.getPersonId()));
            if(b){
                return "用户已存在";
            }
        }
        String path = getPath(file);
        List<FaceObject> faceObjects = FaceDetectUtil.faceDetect(path);
        param.setFeature(faceObjects.getFirst().getFeature());
        addJsonFile(param);
        return "成功";

    }

    public String updateFace(FaceParam param, MultipartFile file, HttpServletRequest request) throws TranslateException, ModelNotFoundException, MalformedModelException, IOException, OrtException {
        boolean check = check(param);
        if(!check){
            return "无该用户";
        }
        delJsonFile(param);
        String path = getPath(file);
        List<FaceObject> faceObjects = FaceDetectUtil.faceDetect(path);
        param.setFeature(faceObjects.getFirst().getFeature());
        addJsonFile(param);
       return "成功";
    }

    public String delFace(FaceParam param, MultipartFile file, HttpServletRequest request) {
        delJsonFile(param);
        return "成功";
    }

    public PersonObject searchFace(FaceParam param, MultipartFile file, HttpServletRequest request) throws IOException, TranslateException, ModelNotFoundException, MalformedModelException, OrtException {
        PersonObject personObject=new PersonObject();
        List<FaceParam> allJson = getAllJson();
        if(!CollectionUtils.isEmpty(allJson)){
            String path = getPath(file);
            List<FaceObject> faceObjects = FaceDetectUtil.faceDetect(path);
            if (!CollectionUtils.isEmpty(faceObjects)){
                FaceObject faceObject = faceObjects.getFirst();
                Float[] feature = faceObject.getFeature().toArray(new Float[0]);
                FaceParam res=null;
                float ff=0f;
                for (FaceParam v : allJson) {
                    Float[] feature1 = v.getFeature().toArray(new Float[0]);
                    float v1 = FaceSimilarityCalculator.calculateCosineSimilarity(feature, feature1);
                    if(v1>ff){
                        ff=v1;
                        res=v;
                    }
                }
                if(ff>0.8f){
                    personObject.setPersonName(res.getPersonName());
                    personObject.setPersonId(res.getPersonId());
                }
            }
        }
        return personObject;
    }

    public boolean check(FaceParam param){
        List<FaceParam> allJson = getAllJson();
        if(!CollectionUtils.isEmpty(allJson)){
            return allJson.stream().anyMatch(v -> Objects.equals(v.getPersonId(), param.getPersonId()));
        }
        return false;
    }
    public String getPath(MultipartFile file) throws IOException {
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

    public void addJsonFile(FaceParam param){
        String uploadPath = fileProperties.getUploadPath();
        // 将 JSON 写入文件
        try(FileWriter fileWriter =  new FileWriter(uploadPath+"face"+  File.separator + param.getPersonId()+".json")) {
            fileWriter.write(JSONObject.toJSONString(param));
            System.out.println("成功创建 JSON 文件。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void delJsonFile(FaceParam param){
        String uploadPath = fileProperties.getUploadPath();
        String directory = uploadPath+"face"+ File.separator+param.getPersonId()+".json";
        File file=new File(directory);
        if(file.exists()){
            file.delete();
        }
    }
    public List<FaceParam> getAllJson() {
        List<FaceParam> list=new ArrayList<>();
        String uploadPath = fileProperties.getUploadPath();
        String directory = uploadPath+"face";
        File folder = new File(directory);
        // 上传目录不存在，则直接创建
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    try {
                        String fileContent = FileUtils.readFileToString(file, "UTF-8");
                        FaceParam faceParam = JSONObject.parseObject(fileContent, FaceParam.class);
                        list.add(faceParam);
                        System.out.println("读取到 JSON 文件内容：" + fileContent);
                    } catch ( Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return list;
    }
}

package com.neworange.json;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.neworange.onnxruntime.Test.getResFileAbsPath;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2025/1/15 16:06
 * @ description
 */
public class Test {
    static String currentDir = System.getProperty("user.dir") + File.separator + "a";
    public static void main(String[] args) {
        final String resFileAbsPath = getResFileAbsPath("json/");
        System.out.println(resFileAbsPath);
        final CityName cityName = new CityName();
        cityName.setType("FeatureCollection");
        JSONArray citys = new JSONArray();
        File dir = new File(resFileAbsPath);
        List<File> allFileList = new ArrayList<>();
        getAllFile(dir,allFileList);
        for (File file : allFileList) {
             String jsonString = getJsonString(file);
             JSONObject jsonObject = JSONObject.parseObject(jsonString);
            final JSONArray features = jsonObject.getJSONArray("features");
            for (int  i = 0;  i < features.size();  i++) {
                citys.add(features.getJSONObject(i));
            }
        }
        cityName.setFeatures(citys);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(cityName);
        System.out.println(jsonObject);
        object2JsonFile(currentDir+File.separator+"141034.json",jsonObject.toString());
//        System.out.println(cityName.toString());
    }
    public static void object2JsonFile(String finalPath, String jsonFormat) {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(finalPath), StandardCharsets.UTF_8);
            osw.write(jsonFormat);
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static String getJsonString(File path) {
        BufferedReader bufferedReader = null;
        String len = null;
        StringBuilder de = new StringBuilder();
        try {
            bufferedReader = new BufferedReader(new FileReader(path));

            while ((len = bufferedReader.readLine()) != null) {
                de.append(len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        assert de != null;
        return de.toString();
    }
    public static void getAllFile(File fileInput, List<File> allFileList) {
        // 获取文件列表
        File[] fileList = fileInput.listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                // 递归处理文件夹
                // 如果不想统计子文件夹则可以将下一行注释掉
                getAllFile(file, allFileList);
            } else {
                // 如果是文件则将其加入到文件数组中
                allFileList.add(file);
            }
        }
    }

}

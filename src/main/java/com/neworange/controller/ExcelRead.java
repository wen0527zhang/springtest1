package com.neworange.controller;


import cn.idev.excel.EasyExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.neworange.entity.Cameera;
import com.neworange.entity.Device;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/9 17:51
 * @description
 */
public class ExcelRead {
    static String currentDir = System.getProperty("user.dir") + File.separator + "a";
    static String fileName = currentDir + File.separator + "linyou.xlsx";
    public static void main(String[] args) {
        String jsonString = getJsonString(currentDir + File.separator + "cameraInfos.json");
        Device data = JSON.parseObject(jsonString, Device.class);
        List<Cameera> deviceList = data.getDeviceList();
        List<Cameera> cameera = getCameera(fileName);
        deviceList.addAll(cameera);
        List<Cameera> newList= deviceList.stream().collect(Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Cameera::getIPAddress))),
                            ArrayList::new));
        data.setDeviceList(newList);

//        for (Cameera c : data.getDeviceList()) {
//            System.out.println(c);
//        }
      //  String jsonFormat = JSON.toJSONString(data, JSONWriter.Feature.PrettyFormat);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(data);
        String string = jsonObject.toString();
        //System.out.println(data.toString());
        object2JsonFile(currentDir + File.separator + "test.json",string);
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


    public static String getJsonString(String path) {
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

    public static  List<Cameera>  getCameera(String fileName) {
        AnalysisEventListener<Cameera> listener = new AnalysisEventListener<Cameera>() {
            @Override
            public void invoke(Cameera map, AnalysisContext analysisContext) {
                // 处理导入的每一行数据
                //   System.out.println(map.toString());
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                // 导入完成后的操作
            }
        };
        Long deviceNum=6103291004131102115L;

        //第一个1代表sheet1, 第二个1代表从第几行开始读取数据，行号最小值为0
        List<Cameera> objects = EasyExcel.read(fileName, Cameera.class, listener).doReadAllSync();
        for (Cameera object : objects) {
            deviceNum++;
            object.setDeviceID(deviceNum+"");

        }

        return objects;
    }


}

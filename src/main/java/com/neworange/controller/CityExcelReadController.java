package com.neworange.controller;


import cn.idev.excel.EasyExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.util.ListUtils;
import com.alibaba.fastjson2.JSON;
import com.neworange.entity.City;
import com.neworange.service.CityService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/5/13 9:54
 * @description  itrip_area_dic
 */
@Slf4j
@RestController("/city")
public class CityExcelReadController {
    static String currentDir = System.getProperty("user.dir") + File.separator + "a";
    static String fileName = currentDir + File.separator + "area_2023.xlsx";
    static String fileNames = currentDir + File.separator + "city_2023.csv";
    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5000;
    /**
     * 缓存的数据
     */
    private List<City> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    @Resource
    private CityService cityService;
    @GetMapping("/createCitys")
    public void createCitys() {
        AnalysisEventListener<City> listener = new AnalysisEventListener<City>() {
            @Override
            public void invoke(City map, AnalysisContext analysisContext) {
                log.info("解析到一条数据:{}", JSON.toJSONString(map));
                cachedDataList.add(map);
                // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
                if (cachedDataList.size() >= BATCH_COUNT) {
                    cityService.saveBatch(cachedDataList);
                    // 存储完成清理 list
                    cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                cityService.saveBatch(cachedDataList);
                // 导入完成后的操作
                log.info("所有数据解析完成！");
            }
        };
        List<City> objects = EasyExcel.read(fileName, City.class,listener).doReadAllSync();
        System.out.println("620317----"+objects.size());
    }

    //省
    @GetMapping("/getCitysName")
    public void getCitysName() {
        cityService.addprovinceId();
    }
    //市
    @GetMapping("/selectBatchId2")
    public void selectBatchId2() {
        cityService.selectBatchId2();
    }
    //区县
    @GetMapping("/selectAreaId")
    public void selectAreaId() {
        cityService.selectAreaId();
    }
    //乡镇
    @GetMapping("/selectTwonshipId")
    public void selectTwonshipId() {
        cityService.selectTwonshipId();
    }
    //村
    @GetMapping("/selectVillageId")
    public void selectVillageId() {
        cityService.selectVillageId();
    }

    @GetMapping("/updateVillageGetPoint")
    public void updateVillageGetPoint() {
        cityService.updateVillageGetPoint();
    }
}

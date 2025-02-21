package com.neworange.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neworange.entity.City;

import java.util.List;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/5/13 11:20
 * @description
 */
public interface CityService extends IService<City> {
    void  addCitys(List<City> cachedDataList);

    void addprovinceId();

    void selectBatchId2();

    void selectTwonshipId();

    void selectVillageId();

    void selectAreaId();

    void updateVillageGetPoint();


}

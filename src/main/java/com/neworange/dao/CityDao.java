package com.neworange.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neworange.entity.Area;
import com.neworange.entity.City;
import com.neworange.entity.VillageName;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/5/13 11:23
 * @description
 */

@Repository
public interface CityDao  extends BaseMapper<City> {

    void addCitys(List<City> cachedDataList);

    List<City> selectBatchIds();

    void addAreas(List<Area> areaList);

    List<Area> selectBatchId2();

    List<Area> selectTwonshipId();

    List<Area> selectAreaId();

    List<Area> selectVillageId();

    List<VillageName> selectVillageName();

    int updateVillageGetPoint(List<VillageName> list);
}

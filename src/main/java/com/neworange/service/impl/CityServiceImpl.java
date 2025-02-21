package com.neworange.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neworange.dao.CityDao;
import com.neworange.entity.Area;
import com.neworange.entity.City;
import com.neworange.entity.VillageName;
import com.neworange.service.CityService;
import com.neworange.utils.ListSpliter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/5/13 11:21
 * @description
 */
@Service
public class CityServiceImpl extends ServiceImpl<CityDao, City> implements CityService {

    @Autowired
    private CityDao cityDao;

//    @Resource
//    private IService iService;

    @Override
    public void addCitys(List<City> cachedDataList) {
       cityDao.addCitys(cachedDataList);

    }

    @Override
    public void addprovinceId() {
        List<Area> areaList= new ArrayList<>();
       List<City> list= cityDao.selectBatchIds();
       list.forEach(city -> {
           Area a=new Area();
           a.setCode(city.getProvinceId());
           a.setName(city.getProvinceName());
           a.setLevel(1);
           a.setParentCode("0");
           areaList.add(a);
       });
        cityDao.addAreas(areaList);
    }

    @Override
    public void selectBatchId2() {
        List<Area> areas = cityDao.selectBatchId2();
        areas.forEach(key->{
            key.setLevel(2);
            System.out.println(key.toString());
        });
        cityDao.addAreas(areas);
    }

    @Override
    public void selectTwonshipId() {
        //乡镇
        List<Area> areas = cityDao.selectTwonshipId();
        areas.forEach(key->{
            key.setLevel(4);
            System.out.println(key.toString());
        });
        cityDao.addAreas(areas);
    }

    @Override
    public void selectVillageId() {
         int BATCH_COUNT = 10000;
        //村
        List<Area> areas = cityDao.selectVillageId();
        areas.forEach(key->{
            key.setLevel(5);
            System.out.println(key.toString());
        });
        List<List<Area>> groupList = ListSpliter.splitGroupList(areas, BATCH_COUNT);
        groupList.forEach(listsPid->{
            cityDao.addAreas(listsPid);
        });


    }

    @Override
    public void selectAreaId() {
        //区县
        List<Area> areas = cityDao.selectAreaId();
        List<Area> collect = areas.stream().filter(key -> key.getCode() != null).collect(Collectors.toList());

        collect.forEach(key->{
            key.setLevel(3);
            System.out.println(key.toString());
        });
        System.out.println("-----"+collect.size());
        cityDao.addAreas(collect);
    }

    @Override
    public void updateVillageGetPoint() {
        List<VillageName> list= cityDao.selectVillageName();
        System.out.println(list.size());

        List<List<VillageName>> groupList = ListSpliter.splitGroupList(list, 5000);
        groupList.forEach(listsPid->{
            int count= cityDao.updateVillageGetPoint(list);
            System.out.println(count);
        });


    }
}

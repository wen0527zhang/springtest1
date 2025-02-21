package com.neworange.entity;

import lombok.Data;

import java.util.List;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/9 17:49
 * @description
 */
@Data
public class Device {
    private List<Cameera> DeviceList;
    private int deviceNum=2;
    private int updatecamera=1;
}

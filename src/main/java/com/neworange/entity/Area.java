package com.neworange.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/5/13 10:29
 * @description
 */
@Data
@TableName("t_organization_all")
public class Area {
    private String code;
    private String name;
    private String parentCode;
    private int level;
    private String longitude;
    private String latitude;
}

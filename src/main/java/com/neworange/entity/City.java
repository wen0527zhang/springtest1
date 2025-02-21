package com.neworange.entity;

import cn.idev.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/5/13 10:56
 * @description
 */
@Data
@TableName("t_city")
public class City implements Serializable {
    //省

    @TableId(type = IdType.AUTO)
    @ExcelProperty(index = 1)
    private String provinceId;
    @ExcelProperty(index = 0)
    private String provinceName;
    //市
    @ExcelProperty(index = 3)
    private String cityId;
    @ExcelProperty(index = 2)
    private String cityName;
    //区县
    @ExcelProperty(index = 5)
    private String areaId;
    @ExcelProperty(index = 4)
    private String areaName;

    //乡镇
    @ExcelProperty(index = 7)
    private String twonshipId;
    @ExcelProperty(index = 6)
    private String twonshipName;
    //村
    @ExcelProperty(index = 9)
    private String villageId;
    @ExcelProperty(index = 8)
    private String villageName;
    //分类
    @ExcelProperty(index = 10)
    private String province;

    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}

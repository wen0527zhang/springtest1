package com.neworange.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2025/1/21 10:35
 * @ description
 */
@Data
public class Movies {
    private Integer id;
    private String title;
    private double price;
    private List<String> genres;
    private Date startTime;
    private String name;

}

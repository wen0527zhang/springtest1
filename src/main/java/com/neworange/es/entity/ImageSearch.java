package com.neworange.es.entity;

import lombok.Data;

@Data
public class ImageSearch {
    private Float[] vector;
    private String url;
    private String imageId;

}

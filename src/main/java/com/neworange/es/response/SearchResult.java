package com.neworange.es.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchResult {
    private String url;
    private String imageId;
    private Float score;
}
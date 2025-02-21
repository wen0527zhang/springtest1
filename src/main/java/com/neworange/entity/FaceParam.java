package com.neworange.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class FaceParam {
    @Schema(required=true)
    private String personId="0000";
    @Schema(required=true)
    private String personName;
    private List<Float> feature;

}
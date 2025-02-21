package com.neworange.gb28181.domain;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/10 16:14
 * @description
 */
public class CivilCodePo {
    private String code;

    private String name;

    private String parentCode;

    public static CivilCodePo getInstance(String[] infoArray) {
        CivilCodePo civilCodePo = new CivilCodePo();
        civilCodePo.setCode(infoArray[0]);
        civilCodePo.setName(infoArray[1]);
        civilCodePo.setParentCode(infoArray[2]);
        return civilCodePo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
}

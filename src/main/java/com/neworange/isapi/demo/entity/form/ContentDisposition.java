package com.neworange.isapi.demo.entity.form;

import lombok.Data;

/**
 * @author zhengxiaohui
 * @date 2024/1/11 18:44
 * @desc 表单单元
 */
@Data
public class ContentDisposition {
    /**
     * 表单字段类型
     */
    public String contentType;

    /**
     * 表单name属性
     */
    public String name;
    /**
     * 表单name属性对应的value值
     */
    public String nameValue;

    /**
     * 表单类型为文件类型时的filename
     */
    public String filename;
    /**
     * 表单中要下发文件的本地路径
     */
    public String fileLocalPath;
}

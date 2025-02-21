package com.neworange.utils.files;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/6/23 18:25
 * @ description
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DownloadFileInfo {
    
    /**
     * 文件总大小
     */
    private long fSize;

    /**
     * 断点起始位置
     */
    private long pos;

    /**
     * 断点结束位置
     */
    private long last;

    /**
     * rang响应
     */
    private long rangeLength;

    /**
     * range响应
     */
    private String contentRange;
}

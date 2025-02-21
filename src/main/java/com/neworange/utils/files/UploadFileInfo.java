package com.neworange.utils.files;

import lombok.Data;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/6/23 19:07
 * @ description
 */
@Data
public class UploadFileInfo {
    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 上传文件会有多个分片，记录当前为那个分片
     */
    private Integer currentChunk;

    /**
     * 总分片数
     */
    private Integer chunks;
}

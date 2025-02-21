package com.neworange.utils.files;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/6/23 19:06
 * @ description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {
    private long fileSize;

    private String fileName;
}

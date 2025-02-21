package com.neworange.entity;

import lombok.Data;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/6/26 15:20
 * @ description
 */
@Data
public class VideoInfo {
    /**
     * 总帧数
     * **/
    private int lengthInFrames;

    /**
     * 帧率
     * **/
    private double frameRate;

    /**
     * 时长
     * **/
    private double duration;

    /**
     * 视频编码
     */
    private String videoCode;
    /**
     * 音频编码
     */
    private String audioCode;

    private int width;
    private int height;
    private int audioChannel;
    private String md5;
    /**
     * 音频采样率
     */
    private Integer sampleRate;

}

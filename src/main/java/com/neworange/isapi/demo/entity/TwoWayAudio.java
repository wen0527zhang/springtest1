package com.neworange.isapi.demo.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2025/1/3 16:22
 * @ description
 */
@Data
@JacksonXmlRootElement(localName = "TwoWayAudio")
public class TwoWayAudio {
    //音频播放级别
    @JacksonXmlProperty(localName = "audioLevel")
    private int audioLevel;

    //活动商品ID
    @JacksonXmlProperty(localName = "microphoneVolume")
    private int microphoneVolume;
    //音频输出编码类型
    @JacksonXmlProperty(localName = "audioCompressionType")
    private String audioCompressionType;
    //音频采样率
    @JacksonXmlProperty(localName = "audioSamplingRate")
    private float audioSamplingRate;
    //是否广播
    @JacksonXmlProperty(localName = "isBroadcast")
    private Boolean isBroadcast= (Boolean) true;
}

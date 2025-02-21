package com.neworange.isapi.demo.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2025/1/13 10:19
 * @ description
 */
@Data
@JacksonXmlRootElement(localName = "TwoWayAudioSession")
public class TwoWayAudioSession {
    @JacksonXmlProperty(localName = "sessionId")
    private String sessionId;
}

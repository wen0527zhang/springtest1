package com.neworange.gb28181.bean;

import lombok.Data;

import javax.sdp.SessionDescription;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/10 16:37
 * @description
 */
@Data
public class Gb28181Sdp {
    private SessionDescription baseSdb;
    private String ssrc;

    private String mediaDescription;

    public static Gb28181Sdp getInstance(SessionDescription baseSdb, String ssrc, String mediaDescription) {
        Gb28181Sdp gb28181Sdp = new Gb28181Sdp();
        gb28181Sdp.setBaseSdb(baseSdb);
        gb28181Sdp.setSsrc(ssrc);
        gb28181Sdp.setMediaDescription(mediaDescription);
        return gb28181Sdp;
    }
}

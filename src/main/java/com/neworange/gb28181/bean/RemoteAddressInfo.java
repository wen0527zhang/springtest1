package com.neworange.gb28181.bean;

import lombok.Data;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/10 16:35
 * @description
 */
@Data
public class RemoteAddressInfo {
    private String ip;
    private int port;
    public RemoteAddressInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}

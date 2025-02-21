package com.neworange.service;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/10 14:39
 * @description
 */
public interface MessageProcessor {
    // 请求回调方法
    void processMessage(String sender, String message);
    // 请求错误回调方法
    void processError(String errorMessage);
    // 响应回调方法
    void processInfo(String infoMessage);

}

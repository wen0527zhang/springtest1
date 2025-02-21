package com.neworange.service;


import org.w3c.dom.events.EventListener;

import javax.sip.*;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/10 14:40
 * @description
 */
public interface SipListener extends EventListener {
    /**
     * SIP服务端接收消息的方法
     * @param var1
     */
    void processRequest(RequestEvent var1);

    /**
     * SIP服务端接收返回的回调方法
     * @param var1
     */
    void processResponse(ResponseEvent var1);

    /**
     * 处理超时回调方法
     * @param var1
     */
    void processTimeout(TimeoutEvent var1);

    /**
     * 处理IO异常的回调方法
     * @param var1
     */
    void processIOException(IOExceptionEvent var1);

    /**
     *
     * @param var1
     */
    void processTransactionTerminated(TransactionTerminatedEvent var1);

    void processDialogTerminated(DialogTerminatedEvent var1);

}

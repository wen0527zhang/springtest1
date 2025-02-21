package com.neworange.gb28181.transmit.request;

import javax.sip.RequestEvent;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/10 18:50
 * @description  对SIP事件进行处理，包括request， response， timeout， ioException, transactionTerminated,dialogTerminated
 */
public interface ISIPRequestProcessor {
    void process(RequestEvent event);
}

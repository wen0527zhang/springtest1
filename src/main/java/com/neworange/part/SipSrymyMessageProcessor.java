package com.neworange.part;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipProvider;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/16 10:53
 * @description
 */
public interface SipSrymyMessageProcessor {
    /**
     * 接收IPCamera发来的SIP协议消息的时候产生的回调函数
     */
    public void processRequest(RequestEvent requestEvent, AddressFactory addressFactory, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider);

    /**
     * 接收IPCamera发来的SIP协议消息的时候产生的回调函数
     */
    public void processResponse(ResponseEvent responseEvent, AddressFactory addressFactory, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider) throws InvalidArgumentException;

    public void processError(String errorMessage);
}

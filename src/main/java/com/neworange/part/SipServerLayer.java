package com.neworange.part;


import lombok.extern.slf4j.Slf4j;


import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import java.util.Properties;
import java.util.TooManyListenersException;
/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/16 10:45
 * @description
 */
@Slf4j
public  class SipServerLayer implements SipListener {

    public SipSrymyMessageProcessor getMessageProcessor() {
        return messageProcessor;
    }

    public void setMessageProcessor(SipSrymyMessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    private SipSrymyMessageProcessor messageProcessor;

    private SipStack sipStack;

    private SipFactory sipFactory;

    private AddressFactory addressFactory;

    private HeaderFactory headerFactory;

    private MessageFactory messageFactory;

    private SipProvider sipProvider;

    private ListeningPoint tcp;

    private ListeningPoint udp;

    public SipProvider getSipProvider() {
        return sipProvider;
    }

    /**
     * Here we initialize the SIP stack.
     */
    @SuppressWarnings("deprecation")
    public SipServerLayer(String ip, int port) throws PeerUnavailableException,
            TransportNotSupportedException, InvalidArgumentException, ObjectInUseException, TooManyListenersException {
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");
        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", "srymy-GB28181");
        properties.setProperty("javax.sip.IP_ADDRESS", ip);

        /**
         * sip_server_log.log 和 sip_debug_log.log
         * 	public static final int TRACE_NONE = 0;
         public static final int TRACE_MESSAGES = 16;
         public static final int TRACE_EXCEPTION = 17;
         public static final int TRACE_DEBUG = 32;
         */
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "0");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "sip_server_log");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "sip_debug_log");

        sipStack = sipFactory.createSipStack(properties);
        headerFactory = sipFactory.createHeaderFactory();
        addressFactory = sipFactory.createAddressFactory();
        messageFactory = sipFactory.createMessageFactory();

        tcp = sipStack.createListeningPoint(ip,  port, "tcp");
        udp = sipStack.createListeningPoint(ip, port, "udp");
        createListeningPoint(ip,port);
    }

    /**
     * 停止监听
     * @throws ObjectInUseException
     */
    public void deleteListeningPoint() throws ObjectInUseException {
        if (tcp != null) {
            sipStack.deleteListeningPoint(tcp);
            sipStack.deleteListeningPoint(udp);
        }
    }

    /**
     * 创建SIP端口监听，同时监听UDP和TCP
     * @param ip
     * @param port
     * @throws InvalidArgumentException
     * @throws TransportNotSupportedException
     * @throws ObjectInUseException
     * @throws TooManyListenersException
     */
    public void createListeningPoint(String ip, int port) throws InvalidArgumentException, TransportNotSupportedException, ObjectInUseException, TooManyListenersException {
        tcp = sipStack.createListeningPoint(ip,  port, "tcp");
        udp = sipStack.createListeningPoint(ip, port, "udp");
        sipProvider = sipStack.createSipProvider(tcp);
        sipProvider.addSipListener(this);
        sipProvider = sipStack.createSipProvider(udp);
        sipProvider.addSipListener(this);
    }

    /**
     * This method is called by the SIP stack when a response arrives.
     */
    @Override
    public void processResponse(ResponseEvent responseEvent) {
        try {
            messageProcessor.processResponse(responseEvent, addressFactory, messageFactory, headerFactory, sipProvider);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * SIP服务端接收消息的方法
     * Content 里面是GBK编码
     * This method is called by the SIP stack when a new request arrives.
     */

    @Override
    public void processRequest(RequestEvent requestEvent) {
        messageProcessor.processRequest(requestEvent, addressFactory, messageFactory, headerFactory, sipProvider);
    }


    /**
     * This method is called by the SIP stack when there's no answer to a
     * message. Note that this is treated differently from an error message.
     */
    @Override
    public void processTimeout(TimeoutEvent evt) {
        messageProcessor.processError("Previous message not sent: " + "timeout");
    }

    /**
     * This method is called by the SIP stack when there's an asynchronous
     * message transmission error.
     */
    @Override
    public void processIOException(IOExceptionEvent evt) {
        messageProcessor.processError("Previous message not sent: " + "I/O Exception");
    }

    /**
     * This method is called by the SIP stack when a dialog (session) ends.
     */
    @Override
    public void processDialogTerminated(DialogTerminatedEvent evt) {
        log.info("会话结束：CallId: {}, DialogId: {}",evt.getDialog().getCallId(), evt.getDialog().getDialogId());
    }

    /**
     * This method is called by the SIP stack when a transaction ends.
     */
    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent evt) {
        log.info("Transaction结束,evt: {}",evt);
    }



}

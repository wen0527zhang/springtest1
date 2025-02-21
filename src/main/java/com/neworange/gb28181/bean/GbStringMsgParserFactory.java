package com.neworange.gb28181.bean;

import gov.nist.javax.sip.parser.MessageParser;
import gov.nist.javax.sip.parser.MessageParserFactory;
import gov.nist.javax.sip.stack.SIPTransactionStack;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/10 16:39
 * @description
 */
public class GbStringMsgParserFactory implements MessageParserFactory {
    /**
     * msg parser is completely stateless, reuse isntance for the whole stack
     * fixes https://github.com/RestComm/jain-sip/issues/92
     */

    private static GbStringMsgParser msgParser = new GbStringMsgParser();
    /*
     * (non-Javadoc)
     * @see gov.nist.javax.sip.parser.MessageParserFactory#createMessageParser(gov.nist.javax.sip.stack.SIPTransactionStack)
     */
    public MessageParser createMessageParser(SIPTransactionStack stack) {
        return msgParser;
    }
}

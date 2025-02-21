package com.neworange.controller;

import com.neworange.part.SipServerLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sip.SipFactory;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Request;
import java.util.ArrayList;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/22 16:22
 * @description
 */
@RestController
@RequestMapping("/person")
public class PersonController {
    @Autowired
    private SipServerLayer sipLayer;


    @Value("${sip.ip}")
    String ip;
    @Value("${sip.port}")
    Integer port;
    @Value("${sip.domain}")
    Integer domain;

    @GetMapping("/tabs")
    public String addNode() throws Exception {

        Request request = null;
        // sipuri
        SipURI requestURI = SipFactory.getInstance().createAddressFactory().createSipURI("34020000001320000001", "192.168.1.130");
        // via
        ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = SipFactory.getInstance().createHeaderFactory().createViaHeader(ip, port, "UDP", null);
        viaHeader.setRPort();
        viaHeaders.add(viaHeader);
        // from
        SipURI fromSipURI = SipFactory.getInstance().createAddressFactory().createSipURI("34020000001320000002", "192.168.1.130");
        Address fromAddress = SipFactory.getInstance().createAddressFactory().createAddress(fromSipURI);
        FromHeader fromHeader = SipFactory.getInstance().createHeaderFactory().createFromHeader(fromAddress, null);
        // to
        SipURI toSipURI = SipFactory.getInstance().createAddressFactory().createSipURI("34020000001320000010", "192.168.1.130");
        Address toAddress = SipFactory.getInstance().createAddressFactory().createAddress(toSipURI);
        ToHeader toHeader = SipFactory.getInstance().createHeaderFactory().createToHeader(toAddress, null);

        // Forwards
        MaxForwardsHeader maxForwards = SipFactory.getInstance().createHeaderFactory().createMaxForwardsHeader(70);
        // ceq
        CSeqHeader cSeqHeader = SipFactory.getInstance().createHeaderFactory().createCSeqHeader(1L, Request.MESSAGE);
        CallIdHeader callIdHeader = sipLayer.getSipProvider().getNewCallId();
        request = SipFactory.getInstance().createMessageFactory().createRequest(requestURI, Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards);

        // request.addHeader(SipUtils.createUserAgentHeader(gitUtil));

        ContentTypeHeader contentTypeHeader = SipFactory.getInstance().createHeaderFactory().createContentTypeHeader("Application", "MANSCDP+xml");
        request.setContent("111111", contentTypeHeader);
        sipLayer.getSipProvider().sendRequest(request);

        return null;
    }

}

package com.neworange.part;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Calendar;
import java.util.UUID;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/16 10:54
 * @description
 */
@Slf4j
@Service
@Order
public class SipSrymyMessageProcessorImpl implements SipSrymyMessageProcessor {
    @Value("${sip.id}")
    String deviceId;
    @Value("${gb28181.password:123456}")
    String password;
    @Override
    public void processRequest(RequestEvent requestEvent, AddressFactory addressFactory, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider) {
        Request request = requestEvent.getRequest();
        if (null == request) {
            log.error("processRequest RequestEvent is null");
            return;
        }
        switch (request.getMethod().toUpperCase()) {
            case Request.MESSAGE:
                log.debug("收到MESSAGE的请求");
                doRequestMessage(requestEvent, addressFactory,messageFactory, headerFactory, sipProvider);
                break;
            case Request.REGISTER:
                log.info("收到下级域REGISTER的请求");
                doRequestRegister(requestEvent, addressFactory, messageFactory, headerFactory, sipProvider);
                break;
            case Request.ACK:
                log.info("收到ACK的请求");
                doRequestAsk(requestEvent, addressFactory, messageFactory, headerFactory, sipProvider);
                break;
            case Request.BYE:
                log.info("收到BYE的请求");
                doRequestBye(requestEvent, addressFactory,messageFactory, headerFactory, sipProvider);
                break;
            case Request.INVITE:
                log.info("收到上级域INVITE的请求");

                doRequestInvite(requestEvent, addressFactory, messageFactory, headerFactory, sipProvider);
                break;
            default:
                log.info("不处理的requestMethod：" + request.getMethod().toUpperCase());
        }
    }

    @Override
    public void processResponse(ResponseEvent responseEvent, AddressFactory addressFactory, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider) throws InvalidArgumentException {
        Response response = responseEvent.getResponse();
        try {
            CSeqHeader cSeqHeader = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
            switch (cSeqHeader.getMethod().toUpperCase()) {
                case Request.MESSAGE:
                    log.debug("收到MESSAGE的请求");
                    doResponseMessage(responseEvent, messageFactory, headerFactory, sipProvider);
                    break;
                case Request.REGISTER:
                    log.info("收到REGISTER的返回");
                    doResponseRegister(responseEvent, addressFactory, messageFactory, headerFactory, sipProvider, response);
                    break;
                case Request.INVITE:
                    log.info("收到INVITE的返回");
                    doResponseInvite(responseEvent, addressFactory, messageFactory, headerFactory, sipProvider, response, cSeqHeader);
                    break;
                case Request.ACK:
                    log.info("收到ACK的返回");
                    break;
                case Request.BYE:
                    log.info("收到BYE的返回");
                    doResponseBye(responseEvent, addressFactory, messageFactory, headerFactory, sipProvider, response);
                    break;
                default:
                    log.info("不处理的requestMethod：" + cSeqHeader.getMethod().toUpperCase());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void doResponseInvite(ResponseEvent responseEvent, AddressFactory addressFactory, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider, Response response, CSeqHeader cSeqHeader) {
    }

    private void doRequestInvite(RequestEvent requestEvent, AddressFactory addressFactory, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider) {
    }

    private void doRequestBye(RequestEvent requestEvent, AddressFactory addressFactory, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider) {

    }

    private void doResponseRegister(ResponseEvent responseEvent, AddressFactory addressFactory, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider, Response response) {
    }

    private void doResponseMessage(ResponseEvent responseEvent, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider) {
    }

    @Override
    public void processError(String errorMessage) {

    }

    private void doResponseBye(ResponseEvent responseEvent, AddressFactory addressFactory, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider, Response response) {
    }

    private void doRequestAsk(RequestEvent requestEvent, AddressFactory addressFactory, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider) {
    }

    private void doRequestRegister(RequestEvent requestEvent, AddressFactory addressFactory, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider) {
        Request request = requestEvent.getRequest();
        try {
            String deviceId = getDeviceIdByRequest(request);
            log.info("Register deviceId is {}, toURI is {}", deviceId);
            if (StringUtils.isEmpty(deviceId)) {
                log.error("Register error, deviceId is empty!");
                return;
            }
            //无需鉴权或者鉴权判断通过
            if (isAuthClosed(deviceId) || isAuthorizationPass(request)) {
                //返回成功 返回Response.OK
                log.info("Register doSuccess!");
                doSuccess(requestEvent, addressFactory, messageFactory, headerFactory, sipProvider);
            } else if (isRegisterWithoutAuth(request)) {
                doUnAuthorized401(requestEvent, messageFactory, headerFactory, sipProvider, request, deviceId);
            } else {
                doLoginFail403(requestEvent, addressFactory, messageFactory, headerFactory, sipProvider);
            }
        } catch (Exception e) {
            log.error("处理Register请求的时候出错 error, {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void doRequestMessage(RequestEvent requestEvent, AddressFactory addressFactory, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider) {
    }

    /**
     * 组装登录成功200的Response
     * @return
     */
    private void doSuccess(RequestEvent requestEvent, AddressFactory addressFactory, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider) throws ParseException, SipException, InvalidArgumentException {
        Request request = requestEvent.getRequest();
        Response response = null;
        try {
            response = messageFactory.createResponse(Response.OK, request);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        DateHeader dateHeader = headerFactory.createDateHeader(Calendar.getInstance());
        response.addHeader(dateHeader);
        ServerTransaction serverTransactionId = requestEvent.getServerTransaction() == null ? sipProvider.getNewServerTransaction(request) : requestEvent.getServerTransaction();
        serverTransactionId.sendResponse(response);
    }

    /**
     * 组装登录失败403的Response
     * @return
     */
    private void doLoginFail403(RequestEvent requestEvent, AddressFactory addressFactory, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider) throws ParseException, SipException, InvalidArgumentException {
        Request request = requestEvent.getRequest();
        Response response = messageFactory.createResponse(Response.FORBIDDEN, request);
        DateHeader dateHeader = headerFactory.createDateHeader(Calendar.getInstance());
        response.addHeader(dateHeader);
        ServerTransaction serverTransactionId = requestEvent.getServerTransaction() == null ? sipProvider.getNewServerTransaction(request) : requestEvent.getServerTransaction();
        serverTransactionId.sendResponse(response);
    }

    private String getDeviceIdByRequest(Request request) {
        ToHeader toHead = (ToHeader) request.getHeader(ToHeader.NAME);
        SipURI toUri =(SipURI) toHead.getAddress().getURI();
        return toUri.getUser();
    }

    /**
     * 判断鉴权是否关闭
     * @param deviceId 设备ID
     * @return
     */
    private boolean isAuthClosed(String deviceId) {
        return (deviceId.equals(deviceId));
    }

    /**
     * 是否校验鉴权通过
     * @param request
     * @return true 通过
     */
    private boolean isAuthorizationPass(Request request) {
        if (isRegisterWithoutAuth(request)) {
            return false;
        }
        AuthorizationHeader authorizationHeader = (AuthorizationHeader) request.getHeader(AuthorizationHeader.NAME);
        String username = authorizationHeader.getUsername();
        String realm = authorizationHeader.getRealm();
        String nonce = authorizationHeader.getNonce();
        URI uri = authorizationHeader.getURI();
        String res = authorizationHeader.getResponse();
        String algorithm = authorizationHeader.getAlgorithm();
        log.info("Authorization信息：username=" + username + ",realm=" + realm + ",nonce=" + nonce + ",uri=" + uri + ",response=" + res + ",algorithm=" + algorithm);
        if (null == username || null == realm || null == nonce || null == uri || null == res || null == algorithm) {
            log.info("Authorization信息不全，无法认证。");
            return false;
        } else {
            // 比较Authorization信息正确性
            String A1 = MD5Util.MD5(username +":"+ realm +":"+ password);
            String A2 = MD5Util.MD5("REGISTER" +":"+  uri);
            String resStr = MD5Util.MD5(A1 +":"+  nonce +":"+  A2);
            return resStr.equals(res);
        }
    }


    private void doUnAuthorized401(RequestEvent requestEvent, MessageFactory messageFactory, HeaderFactory headerFactory, SipProvider sipProvider, Request request, String deviceId) throws ParseException, SipException, InvalidArgumentException {
        Response response;
        response = messageFactory.createResponse(Response.UNAUTHORIZED, request);
        String realm = generateShortUUID();

        String callId = getCallIdFromRequest(request);
        String nonce = MD5Util.MD5(callId + deviceId);
        WWWAuthenticateHeader wwwAuthenticateHeader = headerFactory.createWWWAuthenticateHeader("Digest realm=\"" + realm + "\",nonce=\"" + nonce + "\",algorithm=MD5");
        response.setHeader(wwwAuthenticateHeader);
        ServerTransaction serverTransactionId = requestEvent.getServerTransaction() == null ? sipProvider.getNewServerTransaction(request) : requestEvent.getServerTransaction();
        serverTransactionId.sendResponse(response);
    }


    /**
     * 没有Auth信息，一般在第一次Register的时候
     *
     * @param request
     * @return
     */
    private boolean isRegisterWithAuth(Request request) {
        int expires = request.getExpires().getExpires();
        AuthorizationHeader authorizationHeader = (AuthorizationHeader) request.getHeader(AuthorizationHeader.NAME);
        return expires > 0 && authorizationHeader != null;
    }

    /**
     * 有Auth信息，一般在第二次Register的时候，这个时候会带着第一次服务端返回的Digest信息
     *
     * @param request
     * @return
     */
    private boolean isRegisterWithoutAuth(Request request) {
        int expires = request.getExpires().getExpires();
        AuthorizationHeader authorizationHeader = (AuthorizationHeader) request.getHeader(AuthorizationHeader.NAME);
        return expires > 0 && authorizationHeader == null;
    }

    private String getCallIdFromRequest(Request request) {
        CallIdHeader callIdHeader = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
        return callIdHeader.getCallId();
    }


    public static String generateShortUUID() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }
    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};
}

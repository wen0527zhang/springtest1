package com.neworange.textclient;

import javax.sip.InvalidArgumentException;
import javax.sip.PeerUnavailableException;
import javax.sip.TransportNotSupportedException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TooManyListenersException;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/11 16:24
 * @description
 */
public class Test {
    public static void main(String[] args) {
        String username = "name";
        int port = 8001;
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            SipLayer sipLayer = new SipLayer(username, ip, port);
            //请求行



        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (TransportNotSupportedException e) {
            throw new RuntimeException(e);
        } catch (TooManyListenersException e) {
            throw new RuntimeException(e);
        } catch (InvalidArgumentException e) {
            throw new RuntimeException(e);
        } catch (PeerUnavailableException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {

        }

    }
}

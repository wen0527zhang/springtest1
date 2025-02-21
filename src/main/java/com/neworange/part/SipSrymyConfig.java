package com.neworange.part;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sip.PeerUnavailableException;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/16 11:02
 * @description
 */
@Slf4j
@Configuration
public class SipSrymyConfig {

    @Value("${sip.ip}")
    String ip;
    @Value("${sip.port}")
    Integer port;
    @Value("${sip.domain}")
    Integer domain;

    @Bean
    public SipServerLayer sipLayer(SipSrymyMessageProcessor messageProcessor) {
        try {
            SipServerLayer sipServerLayer = new SipServerLayer(ip, port) ;
            sipServerLayer.setMessageProcessor(messageProcessor);
            log.info("SIP服务启动完毕, 已经在[{}:{}]端口监听SIP国标消息", ip, port);
            return sipServerLayer;
        } catch (PeerUnavailableException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

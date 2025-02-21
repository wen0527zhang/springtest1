package com.neworange.isapi.demo.function.basic.guard;


import com.neworange.isapi.demo.entity.DeviceInfoDTO;
import com.neworange.isapi.demo.entity.DeviceLinkStatusDTO;
import com.neworange.isapi.demo.entity.enums.HttpTypeEnum;
import com.neworange.isapi.demo.function.basic.AlarmGuardDemo;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author zhengxiaohui
 * @date 2024/1/10 10:43
 * @desc 长链接线程
 */
public class LongLinkThread implements Runnable {

    private DeviceInfoDTO deviceInfo;
    private String url;
    private CloseableHttpAsyncClient closeableHttpAsyncClient;

    private List<Character> chBuffer = new ArrayList<>();

    /**
     * 是否订阅
     */
    private boolean subscribe = false;
    /**
     * 订阅的具体事件
     */
    private String subEvent;

    public LongLinkThread(DeviceInfoDTO deviceInfo) {
        this.deviceInfo = deviceInfo;
        this.subscribe = false;
        this.subEvent = null;
        this.url = (deviceInfo.getHttpType() == HttpTypeEnum.TYPE_HTTP ? "http://" : "https://")
                + deviceInfo.getDevIp()
                + ":"
                + deviceInfo.getDevPort()
                + "/ISAPI/Event/notification/alertStream";
    }

    public LongLinkThread(DeviceInfoDTO deviceInfo, boolean subscribe, String subEvent) {
        this.deviceInfo = deviceInfo;
        this.subscribe = subscribe;
        this.subEvent = subEvent;
        if (this.subscribe) {
            // 订阅链接
            this.url = (deviceInfo.getHttpType() == HttpTypeEnum.TYPE_HTTP ? "http://" : "https://")
                    + deviceInfo.getDevIp()
                    + ":"
                    + deviceInfo.getDevPort()
                    + "/ISAPI/Event/notification/subscribeEvent";
        } else {
            // 布防链接
            this.url = (deviceInfo.getHttpType() == HttpTypeEnum.TYPE_HTTP ? "http://" : "https://")
                    + deviceInfo.getDevIp()
                    + ":"
                    + deviceInfo.getDevPort()
                    + "/ISAPI/Event/notification/alertStream";
        }


    }

    @Override
    public void run() {
        // 初始化http长链接，设置摘要认证信息
        httpAysncInit(deviceInfo.getUsername(), deviceInfo.getPassword());

        longLink();
    }

    private void httpAysncInit(String user, String password) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
        closeableHttpAsyncClient = HttpAsyncClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
    }

    private void longLink() {
        DeviceLinkStatusDTO deviceLongLinkStatus = AlarmGuardDemo.devLinkStatusMap.get(this.deviceInfo);
        deviceLongLinkStatus.stopLink.set(false);
        AlarmGuardDemo.devLinkStatusMap.put(this.deviceInfo, deviceLongLinkStatus);

        chBuffer.clear();
        try {
            // 建立回调功能
            FutureCallback<Boolean> callback = new FutureCallback<Boolean>() {
                public void cancelled() {
                    // TODO Auto-generated method stub
                    System.out.println("cancelled");
                }

                public void completed(Boolean arg0) {
                    // TODO Auto-generated method stub
                    System.out.println("completed");
                }

                public void failed(Exception arg0) {
                    // TODO Auto-generated method stub
                    System.out.println("failed");
                }
            };
            // 打开连接
            closeableHttpAsyncClient.start();
            // 重新连接查询线程并且设置超时
            ReConnect recn = new ReConnect(closeableHttpAsyncClient, deviceInfo, chBuffer);
            // 在新的线程中执行recn对象的run()方法，实现多线程并发执行的效果
            Thread rethread = new Thread(recn);
            rethread.start();

            // 连接
            if (this.subscribe) {
                // 订阅逻辑
                String requestBody;
                // 设置订阅条件
                if (subEvent.equals("all")) {
                    requestBody =
                            "<SubscribeEvent xmlns=\"http://www.isapi.org/ver20/XMLSchema\" version=\"2.0\">\n" +
                                    "    <heartbeat>5</heartbeat>\n" +
                                    "    <eventMode>all</eventMode>\n" +
                                    "</SubscribeEvent>";
                } else {
                    // 这里只演示单个event类型的订阅逻辑，订阅多个事件时请自行修改参考ISAPI文档描述修改代码.
                    requestBody =
                            "<SubscribeEvent>\n" +
                            "    <heartbeat>5</heartbeat>\n" +
                            "    <channelMode>list</channelMode>\n" +
                            "    <eventMode>list</eventMode>\n" +
                            "    <EventList>\n" +
                            "        <Event>\n" +
                            "            <type>IDCardInfoEvent</type>\n" +
                            "            <minorEvent>0x69,0x70,0x71</minorEvent>\n" +
                            "            <pictureURLType>binary</pictureURLType>\n" +
                            "        </Event>\n" +
                            "    </EventList>\n" +
                            "</SubscribeEvent>";
                }
                //The request message establishing the connection
                HttpPost httpPost = new HttpPost(url);
                HttpEntity inboundInfoEntity = new StringEntity(requestBody, "UTF-8");
                httpPost.setEntity(inboundInfoEntity);
                HttpAsyncRequestProducer producer = HttpAsyncMethods.create(httpPost);
                //Request connection transfer
                Future<Boolean> future = closeableHttpAsyncClient.execute(
                        producer, new ResponseConsumer(closeableHttpAsyncClient, deviceInfo, chBuffer), callback);
                Boolean result = future.get();
                if (result != null && result.booleanValue()) {
                    System.out.println("Subscribe request successfully executed");
                } else {
                    System.out.println("Subscribe request failed");
                }
                System.out.println("Shutting down");
            } else {
                // 布防逻辑
                Future<Boolean> future = closeableHttpAsyncClient.execute(
                        HttpAsyncMethods.createGet(url),
                        new ResponseConsumer(closeableHttpAsyncClient, deviceInfo, chBuffer),
                        callback);
                Boolean result = future.get();
                if (result != null && result) {
                    System.out.println("Guard request successfully executed!");
                } else {
                    System.out.println("Guard request failed!");
                }
            }
            System.out.println("Shutting down");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return;
    }


}

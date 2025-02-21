package com.neworange.isapi.demo.function.basic.guard;


import com.neworange.isapi.demo.entity.DeviceInfoDTO;
import com.neworange.isapi.demo.entity.DeviceLinkStatusDTO;
import com.neworange.isapi.demo.function.basic.AlarmGuardDemo;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.util.List;

/**
 * @author zhengxiaohui
 * @date 2024/1/10 11:30
 * @desc
 */
public class ReConnect extends Thread {

    private CloseableHttpAsyncClient closeableHttpAsyncClient;
    private int reconnect = 3;
    private int timeout = 10000;
    private DeviceInfoDTO deviceInfo;

    private List<Character> chBuffer;

    public ReConnect(CloseableHttpAsyncClient closeableHttpAsyncClient,
                     DeviceInfoDTO deviceInfo,
                     List<Character> chBuffer) {
        this.closeableHttpAsyncClient = closeableHttpAsyncClient;
        this.deviceInfo = deviceInfo;
        this.chBuffer = chBuffer;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            DeviceLinkStatusDTO devLongLinkStatus = AlarmGuardDemo.devLinkStatusMap.get(deviceInfo);

            if (!devLongLinkStatus.dataRecv.get()) {
                if (this.timeout == 0) {
                    if (this.reconnect == 0) {
                        this.closeableHttpAsyncClient.close();
                    } else {
                        //EN:Timeout reconnect, clear buffer, flag bit initialization, close connection, open connection
                        //CN:超时重新连接，清除缓冲区，标志位初始化，关闭连接，打开连接
                        this.chBuffer.clear();
                        devLongLinkStatus.stopLink.set(false);
                        AlarmGuardDemo.devLinkStatusMap.put(deviceInfo, devLongLinkStatus);
                        this.timeout = 100000;
                        this.closeableHttpAsyncClient.close();
                        this.closeableHttpAsyncClient.start();
                        this.reconnect--;
                    }
                } else {
                    sleep(10);
                    this.timeout -= 10;
                }
            } else {
                return;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

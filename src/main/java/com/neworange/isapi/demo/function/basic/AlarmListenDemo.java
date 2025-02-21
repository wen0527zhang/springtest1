package com.neworange.isapi.demo.function.basic;

import com.neworange.isapi.demo.function.basic.listen.ListenThread;
import lombok.extern.slf4j.Slf4j;


/**
 * @author zhengxiaohui
 * @date 2024/1/15 19:13
 * @desc ISAPI监听示例demo代码
 */
@Slf4j
public class AlarmListenDemo {

    private static Thread listenThread;

    /**
     * 开始监听
     */
    public void startListen() {
        stopListen();

        AlarmListenDemo.listenThread = new Thread(new ListenThread());
        AlarmListenDemo.listenThread.start();
    }

    /**
     * 停止监听
     */
    public void stopListen() {
        if (AlarmListenDemo.listenThread == null) {
            return;
        }
        AlarmListenDemo.listenThread.interrupt();
    }
}

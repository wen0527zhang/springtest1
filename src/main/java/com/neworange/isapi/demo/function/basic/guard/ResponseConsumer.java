package com.neworange.isapi.demo.function.basic.guard;


import com.neworange.isapi.demo.entity.DeviceInfoDTO;
import com.neworange.isapi.demo.entity.DeviceLinkStatusDTO;
import com.neworange.isapi.demo.entity.enums.ContentTypeEnum;
import com.neworange.isapi.demo.function.basic.AlarmGuardDemo;
import com.neworange.isapi.utils.AlarmDataParser;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zhengxiaohui
 * @date 2024/1/10 11:39
 * @desc
 */

public class ResponseConsumer extends AsyncCharConsumer<Boolean> {
    private CloseableHttpAsyncClient closeableHttpAsyncClient;

    private DeviceInfoDTO deviceInfo;

    private final List<Character> chBuffer;

    private AlarmDataParser alarmDataParser = new AlarmDataParser();

    public ResponseConsumer(CloseableHttpAsyncClient closeableHttpAsyncClient,
                            DeviceInfoDTO deviceInfo,
                            List<Character> chBuffer) {
        this.closeableHttpAsyncClient = closeableHttpAsyncClient;
        this.deviceInfo = deviceInfo;
        this.chBuffer = chBuffer;
    }

    //CN:执行延迟任务和定期任务的线程池
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    //CN:表示计划执行的任务
    private ScheduledFuture<?> timeoutFuture;

    // 消息类型
    private int curHttpContentType;

    /**
     * 实际的boundary类型
     */
    private String curHttpBoundary;

    // 取消超时任务
    private void cancelTimeout() {
        if (timeoutFuture != null) {
            timeoutFuture.cancel(false);
        }
    }

    private void startTimeoutTask() {
        timeoutFuture = executor.schedule(() -> {
//            StringBuilder sb = new StringBuilder();
//            for (Character ch : chBuffer) {
//                sb.append(ch);
//            }
//            System.out.println("startTimeoutTask: " + TimeFormatUtil.curTimeFormat() + "\n" +
//                    "chBuffer.size: " + chBuffer.size() + "\n" +
//                    "chBuffer content: \n" + sb.toString());

            // EN:30s If no heartbeat or event is received, the timeout exception is thrown and the link is closed
            // CN:30s未收到心跳或事件，抛出超时异常并关闭链接
            System.out.println("Timeout No heartbeat or event was received");
            DeviceLinkStatusDTO devLongLinkStatus = AlarmGuardDemo.devLinkStatusMap.get(deviceInfo);
            devLongLinkStatus.stopLink.set(true);
            devLongLinkStatus.dataRecv.set(false);
            AlarmGuardDemo.devLinkStatusMap.put(deviceInfo, devLongLinkStatus);
        }, 30, TimeUnit.SECONDS);
    }

    @Override
    protected void releaseResources() {
        //EN:Clearing resources
        // CN:清理资源
        cancelTimeout();
        executor.shutdown();
    }

    @Override
    protected void onResponseReceived(final HttpResponse response) {
        // 确定消息类型
        String tbuf = response.toString();
        System.out.println("onResponseReceived: " + tbuf);
        curHttpContentType = ContentTypeEnum.getEventType(tbuf);
        // 获取实际的boundary信息
        for (Header headerItem : response.getAllHeaders()) {
            if (headerItem.getName().contains("Content-Type")) {
                String headerValue = headerItem.getValue();
                for (String item : headerValue.split(";")) {
                    if (item.contains(BoundaryDataParser.BOUNDARY)) {
                        curHttpBoundary = item.split("=")[1];
                    }
                }
            }
        }
    }

    // 接收消息的回调函数
    @Override
    protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl) throws IOException {
        // 延迟任务逻辑：在延迟时间内接收到数据则重新开始计算延时时间，避免异步接收线程结束。
//        System.out.println("onCharReceived: " + TimeFormatUtil.curTimeFormat());
        cancelTimeout();
        startTimeoutTask();

        DeviceLinkStatusDTO devLongLinkStatus = AlarmGuardDemo.devLinkStatusMap.get(deviceInfo);
        devLongLinkStatus.dataRecv.set(true);
        AlarmGuardDemo.devLinkStatusMap.put(deviceInfo, devLongLinkStatus);

        StringBuilder strBuilder = new StringBuilder();
        // 按照消息类型解析
        switch (curHttpContentType) {
            case ContentTypeEnum.MULTIPART_FORM_DATA: {
                for (int i = 0; i < buf.length(); i++) {
                    // 填充缓冲区
                    chBuffer.add(buf.charAt(i));
                    strBuilder.append(buf.charAt(i));
                }
                if (strBuilder.toString().contains("--" + curHttpBoundary)) {
                    // 一次表单数据解析
                    BoundaryDataParser.parseMultiData(chBuffer);
                }
                break;
            }
            default: {
                System.out.println("未匹配到可以解析的content-type, 请自行处理");
            }
        }

        devLongLinkStatus = AlarmGuardDemo.devLinkStatusMap.get(deviceInfo);
        if (devLongLinkStatus.stopLink.get()) {
            // 停止布防
            buf.clear();
            chBuffer.clear();
            this.close();
            devLongLinkStatus.stopLink.set(true);
            AlarmGuardDemo.devLinkStatusMap.put(deviceInfo, devLongLinkStatus);
        }
    }

    @Override
    protected Boolean buildResult(final HttpContext context) {
        return Boolean.TRUE;
    }
}

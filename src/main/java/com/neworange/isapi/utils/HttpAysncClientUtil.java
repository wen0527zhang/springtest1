package com.neworange.isapi.utils;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class HttpAysncClientUtil {

    public static CloseableHttpAsyncClient httpAsyncclient;

    private static int reconnect = 3;
    private static int timeout = 10000;
    private static boolean stoplink = false;

    private static boolean dataRecv = false;
    private static List<Character> chBuffer = new ArrayList<Character>();
    private static AlarmDataParser AlarmData = new AlarmDataParser();

    //EN:Initializes a long connection communication object
    //CN:初始化一个长连接通信对象
    public static void httpAysncInit(String user, String password) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
        httpAsyncclient = HttpAsyncClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();

    }

    //EN:Long connection function
    //CN:长连接功能
    public static void lonLink(String Url, String event, boolean subscribe) {
        stoplink = false;
        chBuffer.clear();
        try {
            //EN:Set up the callback function
            //CN:建立回调功能
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
            //EN:Open the connection
            //CN:打开连接
            httpAsyncclient.start();

            //EN:Reconnect the query thread with a timeout on
            //CN:重新连接查询线程并且设置超时
            ReConnect recn = new ReConnect();
            //EN:Execute the run () method of the recn object in a new thread to achieve the effect of multi-threaded concurrent execution
            //CN:在新的线程中执行recn对象的run()方法，实现多线程并发执行的效果
            Thread rethread = new Thread(recn);
            rethread.start();


            //EN:connect
            //CN:连接
            Future<Boolean> future = httpAsyncclient.execute(HttpAsyncMethods.createGet(Url), new ResponseConsumer(), callback);
            Boolean result = future.get();
            if (result != null && result) {
                System.out.println("Request successfully executed");
            } else {
                System.out.println("Request failed");
            }
            System.out.println("Shutting down");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return;
    }

    public static void stopLink() throws IOException {
        stoplink = true;
        dataRecv = false;
    }

    //EN:Receive a message
    //CN:接收一个消息
    static class ResponseConsumer extends AsyncCharConsumer<Boolean> {
        //CN:执行延迟任务和定期任务的线程池
        private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        //CN:表示计划执行的任务
        private ScheduledFuture<?> timeoutFuture;
        //EN:Message type
        //CN:消息类型
        public String type;

        //EN:Cancel timeout task
        //CN:取消超时任务
        private void cancelTimeout() {
            if (timeoutFuture != null) {
                timeoutFuture.cancel(false);
//                System.out.println("取消超时任务");
            }
        }

        private void startTimeoutTask() {
//            System.out.println("开始超时任务");
            timeoutFuture = executor.schedule(() -> {
                //EN:30s If no heartbeat or event is received, the timeout exception is thrown and the link is closed
                // CN:30s未收到心跳或事件，抛出超时异常并关闭链接
                try {
                    System.out.println("Timeout No heartbeat or event was received");
                    stopLink();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            //EN:Determine the message type
            //CN:确定消息类型
            System.out.println("onResponseReceived");
            String tbuf = response.toString();
            if (tbuf.contains("multipart")) {
                type = "multipart";
            } else if (tbuf.contains("xml")) {
                type = "xml";
            } else if (tbuf.contains("json")) {
                type = "json";
            }
        }


        //EN:Callback function to receive a message
        //CN:接收消息的回调函数
        @Override
        protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl) throws IOException {
            cancelTimeout();
            startTimeoutTask();
            dataRecv = true;
            //EN:Parsing by message type
            //CN:按照消息类型解析
            if (type.equals("multipart")) {
                int length = buf.length();
                for (int i = 0; i < buf.length(); i++) {
                    //EN:Fill buffer
                    //CN:填充缓冲区
                    chBuffer.add(buf.charAt(i));
                }
                //EN:Form data parsing s
                //CN:表单数据解析
                // FIXME zhengxiaohui 这里需要优化下逻辑
//                new AlarmDataParser().parseMultiData(chBuffer);
            } else if (type.equals("xml")) {
                System.out.println("xml");
            } else if (type.equals("json")) {
                System.out.println("json");
            }
            if (stoplink) {
                //EN:Stop deployment
                //CN:停止布防
                buf.clear();
                chBuffer.clear();
                this.close();
                stoplink = false;
            }
        }

        @Override
        protected Boolean buildResult(final HttpContext context) {
            return Boolean.TRUE;
        }
    }

    //EN:Reconnect the query thread with a timeout
    //CN:使用超时重新连接查询线程
    public static class ReConnect extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                if (!dataRecv) {
                    if (timeout == 0) {
                        if (reconnect == 0) {
                            httpAsyncclient.close();
                        } else {
                            //EN:Timeout reconnect, clear buffer, flag bit initialization, close connection, open connection
                            //CN:超时重新连接，清除缓冲区，标志位初始化，关闭连接，打开连接
                            chBuffer.clear();
                            stoplink = false;
                            timeout = 100000;
                            httpAsyncclient.close();
                            httpAsyncclient.start();
                            reconnect--;
                        }
                    } else {
                        sleep(10);
                        timeout -= 10;
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
}

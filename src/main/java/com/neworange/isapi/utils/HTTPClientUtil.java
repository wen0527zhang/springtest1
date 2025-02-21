package com.neworange.isapi.utils;

import com.neworange.isapi.demo.entity.DeviceInfoDTO;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Http工具类
 */
public class HTTPClientUtil {

    public static CloseableHttpClient httpClient;

    private static final int CONNECT_TIMEOUT = 10 * 1000; //连接超时时间(单位毫秒)
    private static final int SOCKET_TIMEOUT = 10 * 1000;//socket读写超时时间(单位毫秒)
    private static final int MAX_CONN = 100; // 最大连接数
    private static final int Max_PRE_ROUTE = 3 * 1000;
    private static final int MAX_ROUTE = 2 * 1000;

    /**
     * GET操作方法
     *
     * @param deviceInfo 设备信息结构体
     * @param url        调用的isapi地址 如： /ISAPI/xxxx
     */
    public static String doGet(DeviceInfoDTO deviceInfo, String url) {
        String GetUrl = "http://" + deviceInfo.getDevIp() + ":" + deviceInfo.getDevPort() + url;
        HttpGet httpGet = new HttpGet(GetUrl);

        // 设置摘要认证信息
        setDigestAuthInfo(deviceInfo);

        CloseableHttpResponse responseBody = null;
        String response = "";
        try {
            // 由客户端执行(发送)Get请求
            responseBody = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = responseBody.getEntity();
            // System.out.println("响应状态为:" + responseBody.getStatusLine());
            if (responseEntity != null) {
                //   System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                response = EntityUtils.toString(responseEntity);
                //String utf8Response = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                // System.out.println("响应内容为:\n" + utf8Response);

            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (responseBody != null) {
                    responseBody.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * PUT操作命令
     *
     * @param deviceInfo 设备基础信息
     * @param url        调用的isapi地址信息
     * @param input      报文体String格式内容
     * @return
     */
    public static String doPut(DeviceInfoDTO deviceInfo, String url, String input) {
        // 设置摘要认证信息
        setDigestAuthInfo(deviceInfo);
        String putUrl = "http://" + deviceInfo.getDevIp() + ":" + deviceInfo.getDevPort() + url;
        HttpPut httpPut = new HttpPut(putUrl);
        httpPut.setEntity(new StringEntity(input, "UTF-8"));
        CloseableHttpResponse responseBody = null;
        String response = "";
        try {
            // 由客户端执行(发送)Post请求
            responseBody = httpClient.execute(httpPut);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = responseBody.getEntity();
//            System.out.println("响应状态为:" + responseBody.getStatusLine());
//            if (responseEntity != null) {
//                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
//                response = EntityUtils.toString(responseEntity);
//                System.out.println("响应内容为:\n" + response);
//            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (responseBody != null) {
                    responseBody.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * PUT操作命令
     *
     * @param deviceInfo 设备基础信息
     * @param url        调用的isapi地址信息
     * @param input      报文体String格式内容
     * @return
     */
    public static String doPut(DeviceInfoDTO deviceInfo, String url, String input, String contentType) {
        // 设置摘要认证信息
        setDigestAuthInfo(deviceInfo);
        String putUrl = "http://" + deviceInfo.getDevIp() + ":" + deviceInfo.getDevPort() + url;
        HttpPut httpPut = new HttpPut(putUrl);
        httpPut.setHeader("Connection", "keep-alive");
        if ("xml".equals(contentType)) {
            httpPut.setHeader("Content-Type", "application/xml");
        } else {
            httpPut.setHeader("Content-Type", "application/octet-stream");
        }

        httpPut.setEntity(new StringEntity(input, "UTF-8"));
        CloseableHttpResponse responseBody = null;
        String response = "";
        try {
            // 由客户端执行(发送)Post请求
            responseBody = httpClient.execute(httpPut);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = responseBody.getEntity();
//            System.out.println("响应状态为:" + responseBody.getStatusLine());
            if (responseEntity != null) {
//                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                response = EntityUtils.toString(responseEntity);
//                System.out.println("响应内容为:\n" + response);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (responseBody != null) {
                    responseBody.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * POST操作
     *
     * @param deviceInfo 设备基础信息
     * @param url        调用的isapi地址信息
     * @param input      报文体String格式内容
     * @return
     */
    public static String doPost(DeviceInfoDTO deviceInfo, String url, String input) {
        String PostUrl = "http://" + deviceInfo.getDevIp() + ":" + deviceInfo.getDevPort() + url;
        HttpPost httpPost = new HttpPost(PostUrl);
        setRequestConfig(httpPost);
        httpPost.setEntity(new StringEntity(input, "UTF-8"));

        setDigestAuthInfo(deviceInfo);

        CloseableHttpResponse responseBody = null;
        String response = "";
        try {
            // 由客户端执行(发送)Post请求
            responseBody = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = responseBody.getEntity();
            System.out.println("响应状态为:" + responseBody.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                response = EntityUtils.toString(responseEntity);
                String utf8Response = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                System.out.println("响应内容为:\n" + utf8Response);


            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (responseBody != null) {
                    responseBody.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * Delete操作方法
     *
     * @param deviceInfo 设备基础信息
     * @param url        调用的isapi地址信息
     */
    public static String doDelete(DeviceInfoDTO deviceInfo, String url) {
        String DeleteUrl = "http://" + deviceInfo.getDevIp() + ":" + deviceInfo.getDevPort() + url;
        HttpDelete httpDelete = new HttpDelete(DeleteUrl);
        setDigestAuthInfo(deviceInfo);

        CloseableHttpResponse responseBody = null;
        String response = "";
        try {
            // 由客户端执行(发送)Get请求
            responseBody = httpClient.execute(httpDelete);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = responseBody.getEntity();
            System.out.println("响应状态为:" + responseBody.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                response = EntityUtils.toString(responseEntity);
                System.out.println("响应内容为:\n" + response);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (responseBody != null) {
                    responseBody.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }


    /**
     * 设置摘要认证信息
     *
     * @param deviceInfo 设备的基础信息
     */
    private static void setDigestAuthInfo(DeviceInfoDTO deviceInfo) {
        // 设置摘要认证信息
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(deviceInfo.getDevIp(), Integer.parseInt(deviceInfo.getDevPort())),
                new UsernamePasswordCredentials(deviceInfo.getUsername(), deviceInfo.getPassword()));
        httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
    }

    /**
     * 设置基础的http调用参数配置信息
     * 链接超时、请求超时等
     *
     * @param httpRequestBase
     */
    private static void setRequestConfig(HttpRequestBase httpRequestBase) {
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(CONNECT_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();
        httpRequestBase.setConfig(requestConfig);
    }


    public static String doPut(DeviceInfoDTO deviceInfo, String url, String filePath, String contentType, String fileType) {
        // 设置摘要认证信息
        setDigestAuthInfo(deviceInfo);
        File mp3File = new File(filePath);
        String putUrl = "http://" + deviceInfo.getDevIp() + ":" + deviceInfo.getDevPort() + url;
        CloseableHttpResponse responseBody = null;
        String response = "";
        try {

            // 由客户端执行(发送)Post请求
            InputStream inputStream = new FileInputStream(filePath);
            HttpPut httpPut = new HttpPut(putUrl);
            httpPut.setHeader("Connection", "keep-alive");
            if ("xml".equals(contentType)) {
                httpPut.setHeader("Content-Type", "application/xml");
            } else {
                httpPut.setHeader("Content-Type", "application/octet-stream");
            }
            if ("mp3".equals(fileType)) {
                byte[] header = new byte[4];
                // 假设 MP3 文件的长度小于 2^32 - 1
                int fileLength = (int) mp3File.length();
                header[0] = (byte) ((fileLength >> 24) & 0xFF);
                header[1] = (byte) ((fileLength >> 16) & 0xFF);
                header[2] = (byte) ((fileLength >> 8) & 0xFF);
                header[3] = (byte) (fileLength & 0xFF);
                // 创建请求体
                byte[] mp3Data = new byte[fileLength + 4];
                System.arraycopy(header, 0, mp3Data, 0, 4);
                inputStream.read(mp3Data, 4, fileLength);
                HttpEntity entity = new ByteArrayEntity(mp3Data, ContentType.APPLICATION_OCTET_STREAM);
                // 设置请求体
                httpPut.setEntity(entity);
            }else {
                HttpEntity entity = new InputStreamEntity(inputStream, ContentType.APPLICATION_OCTET_STREAM);
                httpPut.setEntity(entity);
            }

            responseBody = httpClient.execute(httpPut);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = responseBody.getEntity();
            System.out.println("响应状态为:" + responseBody.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                response = EntityUtils.toString(responseEntity);
                System.out.println("响应内容为:\n" + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (responseBody != null) {
                    responseBody.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }
}

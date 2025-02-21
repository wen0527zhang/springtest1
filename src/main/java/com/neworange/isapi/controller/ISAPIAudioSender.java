package com.neworange.isapi.controller;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2025/1/6 16:16
 * @ description
 */
public class ISAPIAudioSender {
    private static final String BASE_URL = "http://172.16.100.11:80/ISAPI";
    private static final String CAPABILITIES_URL = "/System/TwoWayAudio/channels/capabilities";
    private static final String OPEN_URL = "/System/TwoWayAudio/channels/{audioID}/open";
    private static final String AUDIO_DATA_RECEIVE_URL = "/System/TwoWayAudio/channels/{audioID}/audioData";
    private static final String AUDIO_DATA_SEND_URL = "/System/TwoWayAudio/channels/{audioID}/audioData";
    private static final String CLOSE_URL = "/System/TwoWayAudio/channels/{audioID}/close";

    private CloseableHttpClient httpClient;

    public ISAPIAudioSender(String username, String password) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(AuthScope.ANY),
                new UsernamePasswordCredentials(username, password));

        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(new TrustSelfSignedStrategy())
                .build();

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        this.httpClient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultCredentialsProvider(credsProvider)
                .build();
    }

    public String getCapabilities() throws IOException {
        HttpGet httpGet = new HttpGet(BASE_URL + CAPABILITIES_URL);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        }
        return "";
    }

    public String openAudioChannel(int audioID, String encodingType, int volume, int frameRate, int sampleRate) throws IOException {
        String url = BASE_URL + OPEN_URL.replace("{audioID}", String.valueOf(audioID));
        HttpPut httpPut = new HttpPut(url);
        String requestBody = createOpenRequestBody(encodingType, volume, frameRate, sampleRate);
        httpPut.setEntity(new ByteArrayEntity(requestBody.getBytes()));
        httpPut.setHeader("Content-Type", "application/xml");
        try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        }
        return "";
    }

    private String createOpenRequestBody(String encodingType, int volume, int frameRate, int sampleRate) {
        return "<TwoWayAudio>"
                + "<audioLevel>" + 0 + "</audioLevel>"
                + "<microphoneVolume>" + 40 + "</microphoneVolume>"
                + "<audioCompressionType>MP3</audioCompressionType>"
                + "<audioSamplingRate>" + 48.0 + "</audioSamplingRate>"
                + "<isBroadcast>" + true + "</isBroadcast>"
                + "</TwoWayAudio>";
    }

    public void receiveAudioData(int audioID) throws IOException {
        String url = BASE_URL + AUDIO_DATA_RECEIVE_URL.replace("{audioID}", String.valueOf(audioID));
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Content-Type", "application/octet-stream");

        try (CloseableHttpResponse response = httpClient.execute(httpGet, HttpClientContext.create())) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // 处理接收到的音频数据
                byte[] audioData = EntityUtils.toByteArray(entity);
                processAudioData(audioData);
            }
        }
    }

    public void sendAudioData(int audioID, byte[] audioData) throws IOException {
        String url = BASE_URL + AUDIO_DATA_SEND_URL.replace("{audioID}", String.valueOf(audioID));
        HttpPut httpPut = new HttpPut(url);
        httpPut.setHeader("Connection", "keep-alive");
        httpPut.setHeader("Content-Type", "application/octet-stream");

        // 根据编码类型添加帧头
        byte[] dataWithHeader = addFrameHeaderIfNecessary(audioData, "MP3"); // 假设编码类型为MP3
        httpPut.setEntity(new ByteArrayEntity(dataWithHeader));

        try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
            // 处理响应
            final String string = response.getEntity().toString();
            System.out.println(string);
        }
    }

    private byte[] addFrameHeaderIfNecessary(byte[] audioData, String encodingType) {
        if (isVariableLengthEncoding(encodingType)) {
            int length = audioData.length;
            byte[] header = new byte[4];
            header[0] = (byte) ((length >> 24) & 0xFF);
            header[1] = (byte) ((length >> 16) & 0xFF);
            header[2] = (byte) ((length >> 8) & 0xFF);
            header[3] = (byte) (length & 0xFF);

            byte[] dataWithHeader = new byte[audioData.length + 4];
            System.arraycopy(header, 0, dataWithHeader, 0, 4);
            System.arraycopy(audioData, 0, dataWithHeader, 4, audioData.length);
            return dataWithHeader;
        }
        return audioData;
    }

    private boolean isVariableLengthEncoding(String encodingType) {
        return "AAC".equals(encodingType) || "MP2L2".equals(encodingType) || "MP3".equals(encodingType);
    }

    public void closeAudioChannel(int audioID) throws IOException {
        String url = BASE_URL + CLOSE_URL.replace("{audioID}", String.valueOf(audioID));
        HttpPut httpPut = new HttpPut(url);
        try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
            // 处理响应
        }
    }

    private void processAudioData(byte[] audioData) {
        // 处理接收到的音频数据
        // 例如：解码、播放等
        System.out.println("Received audio data: " + audioData.length + " bytes");
    }

    public static void main(String[] args) {
        try {
            ISAPIAudioSender service = new ISAPIAudioSender("admin", "xincheng191213");
            // 示例调用
//            String capabilities = service.getCapabilities();
//            System.out.println(capabilities);

            String openResponse = service.openAudioChannel(1, "MP3", 50, 16000, 44100);
            System.out.println(openResponse);

//            service.receiveAudioData(1);
            File mp3File = new File("E:\\work\\springtest1\\src\\main\\resources\\audio\\a.mp3");
            final byte[] audioData = convertFileToByteArray(mp3File);
            //byte[] audioData = new byte[576]; // 示例音频数据
            service.sendAudioData(1, audioData);

            service.closeAudioChannel(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static byte[] convertFileToByteArray(File file) throws IOException {
        // 获取文件大小
        long length = file.length();

        // 创建一个字节数组来存储文件内容
        byte[] bytes = new byte[(int) length];

        // 使用FileInputStream读取文件内容到字节数组中
        try (FileInputStream fis = new FileInputStream(file)) {
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = fis.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            // 如果没有读取到预期数量的字节，则抛出异常（这通常意味着文件在读取过程中被修改了）
            if (offset < bytes.length) {
                throw new IOException("Could not completely read the file " + file.getName());
            }
        }

        return bytes;
    }
}

package com.neworange.isapi.demo.function.audio;


import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.neworange.isapi.demo.entity.DeviceInfoDTO;
import com.neworange.isapi.demo.entity.TwoWayAudio;
import com.neworange.isapi.demo.entity.TwoWayAudioSession;
import com.neworange.isapi.demo.entity.form.ContentDisposition;
import com.neworange.isapi.utils.*;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author zhengxiaohui
 * @date 2024/1/12 14:27
 * @desc 音频管理
 */
public class AudioManage {

    /**
     * 获取已有的音频文件信息
     *
     * @param deviceInfoDTO 设备信息
     */
    public static void getAudioInfo(DeviceInfoDTO deviceInfoDTO) {

        HTTPClientUtil.doGet(deviceInfoDTO,
                "/ISAPI/AccessControl/EventCardLinkageCfg/CustomAudio?format=json");

    }

    /**
     * 上传自定义音频文件到设备
     *
     * @param deviceInfoDTO 设备信息
     */
    public static void uploadCustomAudioFile(DeviceInfoDTO deviceInfoDTO) throws Exception {
        String audioAbsPath = CommonMethod.getResFileAbsPath("audio/测试音频1.mp3");

        File file = new File(audioAbsPath);

        String fileNamePrefix = null;
        String fileNamePostfix = null;
        int lastDotIndex = file.getName().lastIndexOf(".");
        if (lastDotIndex != -1) {
            fileNamePrefix = file.getName().substring(0, lastDotIndex);
        }
        if (lastDotIndex != -1 && lastDotIndex < file.getName().length() - 1) {
            fileNamePostfix = file.getName().substring(lastDotIndex + 1);
        }

        FileInputStream uploadPic = new FileInputStream(file);
        int iFileLen = uploadPic.available();

        // 构建下发的http请求
        List<ContentDisposition> formDataItemList = new ArrayList<>();
        ContentDisposition fieldItem = new ContentDisposition();
        fieldItem.setContentType("application/json");
        fieldItem.setName("CustomAudioInfo");
        fieldItem.setNameValue("{\"CustomAudioInfo\":{\"customAudioID\":0,\"customAudioName\":\"" + fileNamePrefix + "\",\"audioFileFormat\":\"" + fileNamePostfix + "\",\"audioFileSize\":" + iFileLen + "}}");
        formDataItemList.add(fieldItem);
        ContentDisposition fileItem = new ContentDisposition();
        fileItem.setContentType("audio/mpeg");
        fileItem.setName("file");
        fileItem.setFilename(file.getName());
        fileItem.setFileLocalPath(audioAbsPath);
        formDataItemList.add(fileItem);

        String authorization = CustomDigestCallUtil.digestCall(
                "/ISAPI/AccessControl/EventCardLinkageCfg/CustomAudio?format=json",
                "POST",
                formDataItemList,
                deviceInfoDTO);
        System.out.println(authorization);
    }

    /**
     * 删除单个自定义音频文件
     *
     * @param deviceInfoDTO 设备信息
     */
    public static void deleteCustomAudioFile(DeviceInfoDTO deviceInfoDTO) {

        String fileID = "2";//音频文件ID
        HTTPClientUtil.doDelete(deviceInfoDTO,
                "/ISAPI/AccessControl/EventCardLinkageCfg/CustomAudio/" + fileID + "?format=json");

    }

    /**
     * 播放单个自定义音频文件
     *
     * @param deviceInfoDTO 设备信息
     */
    public static void playCustomAudioFile(DeviceInfoDTO deviceInfoDTO) {

        String fileID = "10001";//音频文件ID
        Map<String, Object> parameter = new HashMap<>();
//        parameter.put("audioOutID","[1]");//audioOutID: 进行音频播放的音频输出口(通道)ID
        String input = ConfigFileUtil.getReqBodyFromTemplate("isapi/audio/PlayCustomAudioFile.json", parameter);
        System.out.println(input);
        HTTPClientUtil.doPut(deviceInfoDTO, "/ISAPI/AccessControl/EventCardLinkageCfg/CustomAudio/" + fileID + "/play?format=json", input);

    }

    /**
     * 停止播放单个自定义音频文件
     *
     * @param deviceInfoDTO 设备信息
     */
    public static void stopPlayCustomAudioFile(DeviceInfoDTO deviceInfoDTO) {

        String fileID = "1";//音频文件ID
        Map<String, Object> parameter = new HashMap<>();
//        parameter.put("audioOutID","[1]");//audioOutID: 进行音频播放的音频输出口(通道)ID
        String input = ConfigFileUtil.getReqBodyFromTemplate("isapi/audio/StopCustomAudioFile.json", parameter);
        System.out.println(input);
        HTTPClientUtil.doPut(deviceInfoDTO,
                "/ISAPI/AccessControl/EventCardLinkageCfg/CustomAudio/" + fileID + "/stop?format=json", input);
    }

    /**
     * TTS语音功能（文本转语音)
     *
     * @param deviceInfoDTO 设备信息
     */
    public static void ttsAudioCfg(DeviceInfoDTO deviceInfoDTO) {

        Map<String, Object> parameter = new HashMap<>();
        parameter.put("TTSContent", "TTS测试");//TTS语音文本
        String inputBody = ConfigFileUtil.getReqBodyFromTemplate("isapi/audio/TTS.json", parameter);
        HTTPClientUtil.doPut(deviceInfoDTO,
                "/ISAPI/AccessControl/EventCardLinkageCfg/TTSAudio?format=json", inputBody);

    }

    public static void sendAudioData(DeviceInfoDTO deviceInfoDTO, File audioFile, String sessionId) {
//        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(30000)
//                .setSoReuseAddress(true).build();
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(deviceInfoDTO.getDevIp(), Integer.parseInt(deviceInfoDTO.getDevPort())),
                new UsernamePasswordCredentials(deviceInfoDTO.getUsername(), deviceInfoDTO.getPassword()));
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        // 启动客户端
        try  {
            // 获取音频文件的类型
            //String audioType = getAudioFileType(audioFile);
            // 读取音频文件并处理
            //byte[] audioData = readAudioFile(audioFile, audioType);
            HttpPut httpPut = new HttpPut("http://" + deviceInfoDTO.getDevIp()+ ":" + deviceInfoDTO.getDevPort()  + "/ISAPI/System/TwoWayAudio/channels/1/audioData?sessionId="+sessionId);
            httpPut.setHeader("Connection", "Keep-Alive");
//            httpPut.setHeader("Accept", "application/octet-stream");
            httpPut.setHeader("Content-Type", "application/octet-stream");
//            httpPut.setHeader("Content-Length",  "0");
//            RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).build();
//            httpPut.setConfig(reqConfig);
            try (FileInputStream fis = new FileInputStream(audioFile)) {
                byte[] audioData = new byte[(int) audioFile.length()];
                fis.read(audioData);
                int length = audioData.length;
                byte[] header = new byte[4];
                header[0] = (byte) ((length >> 24) & 0xFF);
                header[1] = (byte) ((length >> 16) & 0xFF);
                header[2] = (byte) ((length >> 8) & 0xFF);
                header[3] = (byte) (length & 0xFF);

                byte[] dataWithHeader = new byte[audioData.length + 4];
                System.arraycopy(header, 0, dataWithHeader, 0, 4);
                System.arraycopy(audioData, 0, dataWithHeader, 4, audioData.length);
                HttpEntity entity= new ByteArrayEntity(dataWithHeader);
                httpPut.setEntity(entity);
                // 打印结果
//                System.out.println("总长度: " + buffer.getInt());
//                System.out.println("字节数据: ");
//                for (byte b : finalFrame) {
//                    System.out.printf("0x%02X ", b);
//                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            final CloseableHttpResponse execute = httpClient.execute(httpPut);
            System.out.println("Response status: " + execute.getStatusLine());
            System.out.println("Response body: " + EntityUtils.toString(execute.getEntity()));
        } catch (Exception  e) {
            e.printStackTrace();
//            logger.error("请求失败", e);

        }finally {
            // 关闭客户端
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    // 辅助方法：将整数转换为4字节的字节数组（大端序）
    private static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }
    // 在实际应用中，这将涉及到对MP3帧头的解析
    private static int[] getFramePositionsAndLengths(byte[] mp3Data) {
        // 这是一个占位符方法，你需要用实际的帧解析逻辑来替换它
        // 返回值应该是一个整数数组，其中包含帧的起始位置和长度信息（例如：[startPos1, length1, startPos2, length2, ...]）
        return new int[0]; // 占位符返回
    }
    // 将MP3文件读取为字节数组
    private static String getAudioFileType(File file) throws IOException {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".mp3")) {
            return "audio/mp3";
        } else if (fileName.endsWith(".aac")) {
            return "audio/aac";
        } else if (fileName.endsWith(".wav") || fileName.endsWith(".pcm")) {
            return "audio/pcm";  // 假设 WAV 和 PCM 都是定长编码
        } else {
            throw new IllegalArgumentException("Unsupported audio format");
        }
    }
    private static byte[] readAudioFile(File file, String audioType) throws IOException {
        byte[] audioData;
        try (FileInputStream fis = new FileInputStream(file)) {
            audioData = fis.readAllBytes();  // 读取所有字节

            if (audioType.equals("audio/mp3") || audioType.equals("audio/aac")) {
                // 处理变长编码（MP3, AAC）：添加帧数据长度头
                audioData = addFrameLengthHeader(audioData);
            }
        }
        return audioData;
    }
    private static byte[] addFrameLengthHeader(byte[] audioData) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 假设每帧最大 576 字节，这里根据实际情况可能需要调整
        final int frameSize = 1920;
        for (int i = 0; i < audioData.length; i += frameSize) {
            // 获取当前帧的长度
            int frameLength = Math.min(frameSize, audioData.length - i);

            // 写入 4 字节的帧长度头
            try {
                outputStream.write(ByteBuffer.allocate(4).putInt(frameLength).array());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 写入帧数据
            outputStream.write(audioData, i, frameLength);
        }

        return outputStream.toByteArray();
    }

    // 处理变长音频数据的帧头部（如果需要）
    // 假设frameData是音频数据的字节数组，frameLength是音频数据的长度（不包括帧头部）
    public static byte[] addFrameHeader(byte[] frameData, int frameLength) {
        // 帧头部为4字节，大端数据表示帧长度
        byte[] frameHeader = new byte[4];
        frameHeader[0] = (byte) ((frameLength >> 24) & 0xFF);
        frameHeader[1] = (byte) ((frameLength >> 16) & 0xFF);
        frameHeader[2] = (byte) ((frameLength >> 8) & 0xFF);
        frameHeader[3] = (byte) (frameLength & 0xFF);

        // 合并帧头部和音频数据
        byte[] audioFrame = new byte[frameHeader.length + frameData.length];
        System.arraycopy(frameHeader, 0, audioFrame, 0, frameHeader.length);
        System.arraycopy(frameData, 0, audioFrame, frameHeader.length, frameData.length);

        return audioFrame;
    }

    /**
     * 格式化输出JSON字符串
     *
     * @return 格式化后的JSON字符串
     */

    public static void main(String[] args) {
        DeviceInfoDTO deviceInfo = new DeviceInfoDTO();
        deviceInfo.setDevIp("172.16.100.11");
        deviceInfo.setDevPort("80");
        deviceInfo.setUsername("admin");
        deviceInfo.setPassword("xincheng191213");
        deviceInfo.setHttpType(0);
//        final String s4 = HTTPClientUtil.doGet(deviceInfo,
//                "/ISAPI/System/TwoWayAudio/channels/capabilities");
//        System.out.println("capabilities :"+s4);

        TwoWayAudio audio = new TwoWayAudio();
        audio.setAudioLevel(0);
        audio.setMicrophoneVolume(50);
        //PCM
        audio.setAudioCompressionType("MP3");
        audio.setAudioSamplingRate(48);
//        audio.setIsBroadcast(true);
//        System.out.println(javaBeanToXml(audio));
         String s = HTTPClientUtil.doPut(deviceInfo,
                "/ISAPI/System/TwoWayAudio/channels/1/open", javaBeanToXml(audio), "xlm");
        System.out.println("open:" + s);
        TwoWayAudioSession xmlObj = XmlUtil.toObject(s, TwoWayAudioSession.class);
        System.out.println(xmlObj.toString());
        xmlObj.getSessionId();

//        final String s1 = HTTPClientUtil.doGet(deviceInfo,
//                "/ISAPI/System/TwoWayAudio/channels/1/audioData");
//        System.out.println("audioData: "+s1);

       // Replace with your target URL
        File mp3File = new File("C:\\Users\\wen\\Music\\智能外呼录音与文本\\发现违法载人，请您停车.mp3"); // Replace with your MP3 file path
        sendAudioData(deviceInfo,mp3File,xmlObj.getSessionId());

//        CredentialsProvider credsProvider = new BasicCredentialsProvider();
//        credsProvider.setCredentials(new AuthScope(deviceInfo.getDevIp(), Integer.parseInt(deviceInfo.getDevPort())),
//                new UsernamePasswordCredentials(deviceInfo.getUsername(), deviceInfo.getPassword()));
//        final CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
//        HttpPut uploadFile = new HttpPut("http://" + deviceInfo.getDevIp() + ":" + deviceInfo.getDevPort() + "/ISAPI/System/TwoWayAudio/channels/1/audioData");
//        try (httpClient) {
//            ByteArrayOutputStream audioDataStream = new ByteArrayOutputStream();
//            final byte[] mp3Bytes  = readFileToByteArray(mp3File);
////            int frameLength = 576;
//            byte[] audioFrame = addFrameHeader(mp3Bytes, mp3Bytes.length);
////            audioDataStream.write(frameLength);
//            audioDataStream.write(audioFrame);
//            // 将ByteArrayOutputStream转换为InputStream
//            final ByteArrayInputStream inputStream = new ByteArrayInputStream(audioDataStream.toByteArray());
//            // 获取输出流以发送音频数据
//                // 使用NIO通道发送音频数据
//
//                    ByteBuffer buffer = ByteBuffer.allocate(4096);
//                    int bytesRead;
//                    while ((bytesRead = inputStream.read(buffer.array(), 0, 4096)) != -1) {
//                        buffer.limit(bytesRead);
//                        HttpEntity entity = new ByteArrayEntity(buffer.array());
//                        uploadFile.setEntity(entity);
//                        try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
//                            System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
//                            // Handle the response if needed
//                            String responseBody = EntityUtils.toString(response.getEntity());
//                            System.out.println("Response Body: " + responseBody);
//                        }
//                        buffer.clear();
//                    }
//
//
//            // 构建请求体：长度 + 文件内容
////            byte[] buffer = new byte[4096];
////            int bytesRead;
////            while ((bytesRead = inputStream.read(buffer)) != -1) {
////                // Calculate the length of the current chunk
////                int length = bytesRead;
////                // Create a byte array to hold the 4-byte header + the chunk
////                byte[] dataToSend = new byte[length + 4];
////                // Set the 4-byte header (length of the chunk)
////                dataToSend[0] = (byte) ((length >> 24) & 0xFF);
////                dataToSend[1] = (byte) ((length >> 16) & 0xFF);
////                dataToSend[2] = (byte) ((length >> 8) & 0xFF);
////                dataToSend[3] = (byte) (length & 0xFF);
////                // Copy the chunk data into the dataToSend array
////                System.arraycopy(buffer, 0, dataToSend, 4, length);
////                // Create and send the HTTP POST request
////                HttpEntity entity = new ByteArrayEntity(dataToSend);
////                uploadFile.setEntity(entity);
////                try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
////                    System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
////                    // Handle the response if needed
////                    String responseBody = EntityUtils.toString(response.getEntity());
////                    System.out.println("Response Body: " + responseBody);
////                }
////            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                httpClient.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

//        final String s3 = HTTPClientUtil.doGet(deviceInfo,
//                "/ISAPI/System/TwoWayAudio/channels/1/close");
//        System.out.println("close: "+s3);
        final String closes = HTTPClientUtil.doPut(deviceInfo,
                "/ISAPI/System/TwoWayAudio/channels/1/close", javaBeanToXml(audio), "xlm");
        System.out.println("closes:" + closes);
    }

    private static byte[] readFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return data;
        }
    }
    //XML文件头
    private static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";

    public static String javaBeanToXml(Object obj) {
        String xml = "";
        if (Objects.isNull(obj)) {
            return xml;
        }
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xml = xmlMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "";
        }
        // 添加xml文件头
        return XML_HEAD + xml;
    }

}

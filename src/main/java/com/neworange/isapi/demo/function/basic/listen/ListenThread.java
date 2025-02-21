package com.neworange.isapi.demo.function.basic.listen;


import com.neworange.isapi.demo.entity.enums.ContentTypeEnum;
import com.neworange.isapi.utils.AlarmDataParser;
import org.apache.logging.log4j.util.PropertiesUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author zhengxiaohui
 * @date 2024/1/18 17:38
 * @desc 监听处理线程
 */
public class ListenThread implements Runnable {
    static PropertiesUtil propertiesUtil = new PropertiesUtil("./config.properties");
    private final AlarmDataParser alarmDataParser = new AlarmDataParser();

    @Override
    public void run() {
        int listenPort = propertiesUtil.getIntegerProperty("custom.isapi.listen.port", 9999);
        try {
            ServerSocket serverSocket = new ServerSocket(listenPort);
            System.out.println("启动监听, 监听端口:" + listenPort);
            while (!Thread.currentThread().isInterrupted()) {
                Socket accept = serverSocket.accept();
                accept.setKeepAlive(true);
//                System.out.println("设备(客户端)信息：" + accept.getInetAddress().getHostAddress());
                if (accept.isConnected()) {
                    handleData(accept);
                }
                accept.close();
            }
            serverSocket.close();
            System.out.println("停止监听完成");
        } catch (InterruptedException e) {
            // 线程被中断的处理逻辑
            System.out.println("停止监听完成: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("监听创建异常: " + e.getMessage());
        }
    }

    private void handleData(Socket accept) throws Exception {
        InputStream inputData = accept.getInputStream();
        OutputStream outputData = accept.getOutputStream();

        // 输出数据
        ByteArrayOutputStream byOutputData = new ByteArrayOutputStream();

        byte[] buffer = new byte[2 * 1024 * 1024];
        int length = 0;

        // 持续接收处理数据直到接收完毕
        String recvAlarmData = "";
        while ((length = inputData.read(buffer)) > 0) {
            byOutputData.write(buffer, 0, length);

            String recvData = byOutputData.toString();
            recvAlarmData = recvAlarmData + recvData;

            // 获取boundary
            String strBoundary = "boundary=";
            int beginIndex = recvData.indexOf(strBoundary);
            beginIndex += strBoundary.length();
            int lenIndex = recvData.indexOf("\r\n", beginIndex);
            String strBoundaryMark = recvData.substring(beginIndex, lenIndex);

            if (recvAlarmData.contains("--" + strBoundaryMark.trim() + "--")) {
                //表单结束符判断接收结束
                break;
            }
        }

        String response = "HTTP/1.1 200 OK" +
                "\r\n" +
                "Connection: close" +
                "\r\n\r\n";
        outputData.write(response.getBytes());
        outputData.flush();
        outputData.close();
        inputData.close();

        //解析数据
        response = parseAlarmInfoByte(byOutputData);
    }

    private String parseAlarmInfoByte(ByteArrayOutputStream byOutputData) throws Exception {
        // 事件报文字节
        byte[] byAlarmDataInfo = byOutputData.toByteArray();
        int iDataLen = byAlarmDataInfo.length;

        String szBoundaryMark = "boundary=";
        String szContentTypeMark = "Content-Type: ";
        int iTypeMarkLen = szContentTypeMark.getBytes("UTF-8").length;
        String szContentLenMark = "Content-Length: ";
        int iLenMarkLen = szContentLenMark.getBytes("UTF-8").length;
        String szContentLenMark2 = "content-length: ";
        int iLenMarkLen2 = szContentLenMark2.getBytes("UTF-8").length;
        int iContentLen = 0;
        String szEndMark = "\r\n";
        int iMarkLen = szEndMark.getBytes("UTF-8").length;
        String szEndMark2 = "\r\n\r\n";
        int iMarkLen2 = szEndMark2.getBytes("UTF-8").length;
        String szJson = "text/json";
        String szJpg = "image/jpeg";

        int iStartBoundary = doDataSearch(byAlarmDataInfo, szBoundaryMark.getBytes("UTF-8"), 0, byAlarmDataInfo.length);
        iStartBoundary += szBoundaryMark.getBytes("UTF-8").length;
        int iEndBoundary = doDataSearch(byAlarmDataInfo, szEndMark.getBytes("UTF-8"), iStartBoundary, byAlarmDataInfo.length);
        byte[] byBoundary = new byte[iEndBoundary - iStartBoundary];
        System.arraycopy(byAlarmDataInfo, iStartBoundary, byBoundary, 0, iEndBoundary - iStartBoundary);

        String szBoundaryEndMark = "--" + new String(byBoundary).trim() + "--";
        int iDateEnd = doDataSearch(byAlarmDataInfo, szBoundaryEndMark.getBytes("UTF-8"), 0, byAlarmDataInfo.length);

        String szBoundaryMidMark = "--" + new String(byBoundary).trim();
        int iBoundaryMidLen = szBoundaryMidMark.getBytes("UTF-8").length;
        int startIndex = iEndBoundary;

        String szContentType = "";

        int[] iBoundaryPos = new int[11]; //boundary个数，这里最大解析10个
        int iBoundaryNum = 0;
        for (iBoundaryNum = 0; iBoundaryNum < 10; iBoundaryNum++) {
            startIndex = doDataSearch(byAlarmDataInfo, szBoundaryMidMark.getBytes("UTF-8"), startIndex, iDateEnd);
            if (startIndex < 0) {
                break;
            }
            startIndex += iBoundaryMidLen;
            iBoundaryPos[iBoundaryNum] = startIndex;
        }
        iBoundaryPos[iBoundaryNum] = iDateEnd;//最后一个是结束符

        for (int i = 0; i < iBoundaryNum; i++) {
            // Content-Type
            int iStartType = doDataSearch(byAlarmDataInfo, szContentTypeMark.getBytes("UTF-8"), iBoundaryPos[i], iBoundaryPos[i + 1]);
            if (iStartType > 0) {
                iStartType += iTypeMarkLen;
                int iEndType = doDataSearch(byAlarmDataInfo, szEndMark.getBytes("UTF-8"), iStartType, iBoundaryPos[i + 1]);
                if (iEndType > 0) {
                    byte[] byType = new byte[iEndType - iStartType];
                    System.arraycopy(byAlarmDataInfo, iStartType, byType, 0, iEndType - iStartType);
                    szContentType = new String(byType).trim();
                }
            }

            // Content-Length
            int iStartLength = doDataSearch(byAlarmDataInfo, szContentLenMark.getBytes("UTF-8"), iBoundaryPos[i], iBoundaryPos[i + 1]);
            if (iStartLength > 0) {
                iStartLength += iLenMarkLen;
                int iEndLength = doDataSearch(byAlarmDataInfo, szEndMark.getBytes("UTF-8"), iStartLength, iBoundaryPos[i + 1]);
                if (iEndLength > 0) {
                    byte[] byLength = new byte[iEndLength - iStartLength];
                    System.arraycopy(byAlarmDataInfo, iStartLength, byLength, 0, iEndLength - iStartLength);
                    iContentLen = Integer.parseInt(new String(byLength).trim());
                }
            }

            // Content-Length(兼容错误大小写)
            int iStartLength2 = doDataSearch(byAlarmDataInfo, szContentLenMark2.getBytes("UTF-8"), iBoundaryPos[i], iBoundaryPos[i + 1]);
            if (iStartLength2 > 0) {
                iStartLength2 += iLenMarkLen2;
                int iEndLength2 = doDataSearch(byAlarmDataInfo, szEndMark.getBytes("UTF-8"), iStartLength2, iBoundaryPos[i + 1]);
                if (iEndLength2 > 0) {
                    byte[] byLength2 = new byte[iEndLength2 - iStartLength2];
                    System.arraycopy(byAlarmDataInfo, iStartLength2, byLength2, 0, iEndLength2 - iStartLength2);
                    iContentLen = Integer.parseInt(new String(byLength2).trim());
                }
            }

            // 通过\r\n\r\n判断报文数据起始位置
            int iStartData = doDataSearch(byAlarmDataInfo, szEndMark2.getBytes("UTF-8"), iBoundaryPos[i], iBoundaryPos[i + 1]);
            if (iStartData > 0) {
                iStartData += iMarkLen2;

                // 有的报文可能没有Content-Length
                if (iContentLen <= 0) {
                    iContentLen = iBoundaryPos[i + 1] - iStartData;
                }

                // 截取数据内容
                byte[] byData = new byte[iContentLen];
                System.arraycopy(byAlarmDataInfo, iStartData, byData, 0, iContentLen);

                // 根据类型处理数据
                int contentType = ContentTypeEnum.getEventType(szContentType);
                String storeFolder = System.getProperty("user.dir") + "\\output\\listen\\event\\";
                switch (contentType) {
                    case ContentTypeEnum.APPLICATION_JSON:
                    case ContentTypeEnum.APPLICATION_XML: {
                        String rawContent = new String(byData).trim();
                        alarmDataParser.parseAlarmInfo(contentType, storeFolder, rawContent, null);
                        break;
                    }
                    case ContentTypeEnum.IMAGE_JPEG:
                    case ContentTypeEnum.IMAGE_PNG:
                    case ContentTypeEnum.VIDEO_MPG:
                    case ContentTypeEnum.VIDEO_MPEG4:
                    case ContentTypeEnum.APPLICATION_ZIP: {
                        alarmDataParser.parseAlarmInfo(contentType, storeFolder, null, byData);
                        break;
                    }
                    default: {
                        System.out.println("未匹配到可以解析的content-type, 请自行补全处理!");
                    }
                }
            }
        }
        // 响应报文
        String response = "";

        // 消费交易事件 （实际如果没有消费机设备可以不需要消费机的处理代码）
        String eventType = "";
        String eventConfirm = "";
        if (eventType.equals("ConsumptionEvent") || eventType.equals("TransactionRecordEvent") || eventType.equals("HealthInfoSyncQuery")) {
            response = "HTTP/1.1 200 OK" +
                    "\r\n" +
                    "Content-Type: application/json; charset=\"UTF-8\"" +
                    "\r\n" +
                    "Content-Length: " + eventConfirm.length() +
                    "\r\n\r\n" + eventConfirm +
                    "\r\n";
        } else {
            response = "HTTP/1.1 200 OK" +
                    "\r\n" +
                    "Connection: close" +
                    "\r\n\r\n";
        }

        return response;
    }

    private int doDataSearch(byte[] bySrcData, byte[] keyData, int startIndex, int endIndex) {
        if (bySrcData == null || keyData == null || bySrcData.length <= startIndex || bySrcData.length < keyData.length) {
            return -1;
        }

        if (endIndex > bySrcData.length) {
            endIndex = bySrcData.length;
        }

        int iPos, jIndex;
        for (iPos = startIndex; iPos < endIndex; iPos++) {
            if (bySrcData.length < keyData.length + iPos) {
                break;
            }

            for (jIndex = 0; jIndex < keyData.length; jIndex++) {
                if (bySrcData[iPos + jIndex] != keyData[jIndex]) {
                    break;
                }
            }
            if (jIndex == keyData.length) {
                return iPos;
            }
        }
        return -1;
    }

}

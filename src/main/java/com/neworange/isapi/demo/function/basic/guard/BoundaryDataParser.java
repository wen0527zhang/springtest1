package com.neworange.isapi.demo.function.basic.guard;


import com.neworange.isapi.demo.entity.enums.ContentTypeEnum;
import com.neworange.isapi.utils.AlarmDataParser;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author zhengxiaohui
 * @date 2024/2/28 14:42
 * @desc 报文体数据切割处理
 */
public class BoundaryDataParser {
    private static final int HeadSize = 256;
    public static final String END = "\r\n";
    public static final String BOUNDARY = "boundary=";
    public static final String CONTENT_TYPE = "Content-Type: ";
    public static final String CONTENT_LENGTH = "Content-Length: ";
    public static final String CONTENT_ID = "Content-ID: ";
    private static AlarmDataParser alarmDataParser = new AlarmDataParser();


    /**
     * 转换multipart类型的表单数据逻辑
     */
    public static void parseMultiData(List<Character> chBuffer) {
        // [一] 取固定的长度size转换为字符串判断当前boundary中的内容信息
        int offset = 0; // 标定实际报文内容的起始位置
        int infoType = 0; // 单个boundary header头标定的数据类型
        if (chBuffer.isEmpty() || chBuffer.size() < HeadSize) {
            return;
        }
        List<Character> targetList = chBuffer.subList(0, HeadSize);
        StringBuilder targetBuf = new StringBuilder();
        for (char tempNode : targetList) {
            targetBuf.append(tempNode);
        }
        String strHeadBuf = targetBuf.toString();
        // [二] 判断单个boundary的内容类型
        if (strHeadBuf.contains(CONTENT_TYPE)) {
            offset += strHeadBuf.indexOf(CONTENT_TYPE);
            infoType = ContentTypeEnum.getEventType(strHeadBuf);
        }

        // [三] 获取当前要处理的boundary的Content-Length信息用于后续从缓冲区中取数据
        StringBuilder strlen = new StringBuilder();
        int contentLength = 0;
        if (strHeadBuf.contains(CONTENT_LENGTH)) {
            // 如果strHeadBuf中有ContentID字段，对该字段进行解析
            if (strHeadBuf.contains(CONTENT_ID)) {
                offset = strHeadBuf.indexOf(CONTENT_LENGTH);
                offset += CONTENT_LENGTH.length();
                // 获得表单单元的长度
                for (int i = 0; strHeadBuf.charAt(offset) != '\n'; offset++) {
                    strlen.append(strHeadBuf.charAt(offset));
                    i++;
                }
                String str = strlen.toString().trim();
                contentLength = Integer.parseInt(str);

                // 将数据偏移量定位到ContentID字段之后
                offset = strHeadBuf.indexOf(CONTENT_ID);
                for (int i = 0; strHeadBuf.charAt(offset) != '\r'; offset++) {
                    i++;
                }
            } else {
                offset = strHeadBuf.indexOf(CONTENT_LENGTH);
                offset += CONTENT_LENGTH.length();
                // 获得表单单元的长度
                for (int i = 0; strHeadBuf.charAt(offset) != '\r'; offset++) {
                    strlen.append(strHeadBuf.charAt(offset));
                    i++;
                }
                String str = strlen.toString().trim();
                contentLength = Integer.parseInt(str);
            }
        }

        // 定位到offset的所在位置
        offset = strHeadBuf.indexOf(END + "" + END);
        if (-1 == offset) {
            System.out.println("could not found http header and body separate symbol : \\r\\n\\r\\n");
            System.out.println("strHeadBuf: \n" + strHeadBuf + "\n");
            return;
        }
        // \r\n\r\n 到正式的报文体开始为止
        offset += (2 * END.length());
        // 先判断下当前接收到的缓存内容是否在当前boundary的length之内
        //  - 已经接收到boundary单元： 根据类型做代码切割处理
        //  - 未接收完boundary单元： 继续接收
        if (chBuffer.size() < offset + contentLength) {
            return;
        }
        // [四] 从缓冲区中截取Content-Length长度的数据内容
        String strContent = null;
        String strUTF8Content = null;
        byte[] picByte = null;
        switch (infoType) {
            case ContentTypeEnum.APPLICATION_JSON:
            case ContentTypeEnum.APPLICATION_XML: {
                // 从缓冲区中取出boundary的length数据
                StringBuilder strBodyBuf = new StringBuilder();
                targetList = chBuffer.subList(offset, offset + contentLength);
                for (char c : targetList) {
                    strBodyBuf.append(c);
                }
                strContent = strBodyBuf.toString();

                //strContent拿到的字符串编码格式是ISO8859-1，实际ISAPI交互接收到的报文是UTF-8编码的，所以需要做如下转换
                try {
                    strUTF8Content = new String(strContent.getBytes("iso8859-1"),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO 这里请自行处理编码格式转换出错问题, 这里抄底逻辑处理为使用未转换前的str内容
                    System.out.println("iso8859-1编码转UTF-8编码出错");
                    strUTF8Content = strContent;
                }

                // 从缓冲区中移除掉已经取出的数据信息
                removeFromBuffer(chBuffer, offset, contentLength);
                break;
            }
            case ContentTypeEnum.IMAGE_JPEG:
            case ContentTypeEnum.IMAGE_PNG:
            case ContentTypeEnum.VIDEO_MPG:
            case ContentTypeEnum.VIDEO_MPEG4:
            case ContentTypeEnum.APPLICATION_ZIP: {
                if (chBuffer.size() > offset + contentLength) {
                    picByte = new byte[contentLength];
                    targetList = chBuffer.subList(offset, offset + contentLength);
                    for (int i = 0; i < contentLength; i++) {
                        picByte[i] = (byte) targetList.get(i).charValue();
                    }
                    // 从缓冲区中移除掉已经取出的数据信息
                    removeFromBuffer(chBuffer, offset, contentLength);
                }
                break;
            }
            // TODO 如果有其他的类型在这里添加类型处理逻辑
            default: {
                System.out.println("未匹配到可以解析的content-type, 请自行补全处理!");
            }
        }
        // [五] 截取出的数据进行响应的格式转换（json/xml/图片数据等）
        alarmDataParser.parseAlarmInfo(infoType, System.getProperty("user.dir") + "\\output\\guard\\event\\", strUTF8Content, picByte);

        // [六] 解析剩余的数据内容
        parseMultiData(chBuffer);
    }

    /**
     * 从接收缓冲区中移除指定的数据
     *
     * @param offset        移除开始位置
     * @param contentLength 移除内容长度
     */
    public static void removeFromBuffer(List<Character> chBuffer, int offset, int contentLength) {
        for (int i = 0; i < (offset + contentLength + END.length()) && chBuffer.size() > 0; i++) {
            chBuffer.remove(0);
        }
    }

}

package com.neworange.isapi.demo.entity.enums;

/**
 * @author zhengxiaohui
 * @date 2024/1/18 14:19
 * @desc Http ContentType类型
 */
public class ContentTypeEnum {

    public static final int TEXT_HTML = 1; // HTML 文本
    public static final int TEXT_PLAIN = 2; // 纯文本
    public static final int APPLICATION_JSON = 3; // JSON 数据
    public static final int APPLICATION_XML = 4; // XML 数据
    public static final int APPLICATION_X_WWW_FORM_URLENCODED = 5; // URL 编码的表单数据
    public static final int MULTIPART_FORM_DATA = 6; // 多部分表单数据
    public static final int IMAGE_JPEG = 7; // JPEG 图像
    public static final int IMAGE_PNG = 8; // PNG 图像
    public static final int AUDIO_MPEG = 9; // MPEG 音频
    public static final int VIDEO_MP4 = 10; // MP4 视频
    public static final int VIDEO_MPG = 11; // 音频  video/mpg (一般是MP3格式)
    public static final int VIDEO_MPEG4 = 12; // 视频 video/mpeg4  （设备一般是ps封装）
    public static final int APPLICATION_OCTET_STREAM = 13; // 二进制数据流

    public static final int APPLICATION_ZIP = 14; // application/zip

    /**
     * 获取当前demo程序标准化的Content-Type枚举定义
     *
     * @param content 包含ContentType定义的字符串
     * @return
     */
    public static int getEventType(String content) {
        if (content == null) {
            return 0;
        }

        int res = 0;
        int indexOfType = Integer.MAX_VALUE;

        int tempIndex = content.indexOf("json");
        if (tempIndex != -1 && tempIndex <= indexOfType) {
            indexOfType = tempIndex;
            res = ContentTypeEnum.APPLICATION_JSON;
        }

        tempIndex = content.indexOf("xml");
        if (tempIndex != -1 && tempIndex <= indexOfType) {
            indexOfType = tempIndex;
            res = ContentTypeEnum.APPLICATION_XML;
        }

        tempIndex = content.indexOf("jpeg");
        if (tempIndex != -1 && tempIndex <= indexOfType) {
            indexOfType = tempIndex;
            res = ContentTypeEnum.IMAGE_JPEG;
        }

        tempIndex = content.indexOf("png");
        if (tempIndex != -1 && tempIndex <= indexOfType) {
            indexOfType = tempIndex;
            res = ContentTypeEnum.IMAGE_PNG;
        }

        tempIndex = content.indexOf("mpg");
        if (tempIndex != -1 && tempIndex <= indexOfType) {
            indexOfType = tempIndex;
            res = ContentTypeEnum.VIDEO_MPG;
        }

        tempIndex = content.indexOf("mpeg4");
        if (tempIndex != -1 && tempIndex <= indexOfType) {
            indexOfType = tempIndex;
            res = ContentTypeEnum.VIDEO_MPEG4;
        }

        tempIndex = content.indexOf("zip");
        if (tempIndex != -1 && tempIndex <= indexOfType) {
            indexOfType = tempIndex;
            res = ContentTypeEnum.APPLICATION_ZIP;
        }

        tempIndex = content.indexOf("multipart");
        if (tempIndex != -1 && tempIndex <= indexOfType) {
            indexOfType = tempIndex;
            res = ContentTypeEnum.MULTIPART_FORM_DATA;
        }

        if (res == 0) {
            System.out.println("未匹配到可以解析的content-type, 请自行处理！\n" + content);
        }
        return res;
    }

    /**
     * 根据contentType获取要补全的存储类型后缀
     *
     * @param contentType
     * @return
     */
    public static String getFilePostfix(int contentType) {
        if (contentType == 0) {
            return ".data";
        }

        switch (contentType) {
            case ContentTypeEnum.APPLICATION_JSON: {
                return ".json";
            }
            case ContentTypeEnum.APPLICATION_XML: {
                return ".xml";
            }
            case ContentTypeEnum.IMAGE_JPEG: {
                return ".jpeg";
            }
            case ContentTypeEnum.IMAGE_PNG: {
                return ".png";
            }
            case ContentTypeEnum.VIDEO_MPG: {
                return ".mp3";
            }
            case ContentTypeEnum.VIDEO_MPEG4: {
                return ".ps";
            }
            case ContentTypeEnum.APPLICATION_ZIP: {
                return ".zip";
            }
            // TODO 如果有其他的类型在这里添加类型处理逻辑
            default: {
                System.out.println("未匹配到可以解析的content-type, 请自行补全处理!");
                return ".data";
            }
        }
    }
}

package com.neworange.isapi.utils;



import com.neworange.isapi.demo.entity.enums.ContentTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;

/**
 * 报警事件解析处理逻辑
 */
@Slf4j
public class AlarmDataParser {

    /**
     * 解析数据
     *
     * @param contentType   要解析的类型
     * @param storeFolderPath 保存文件路径
     * @param alarmInfo
     * @param byteArray
     */
    public void parseAlarmInfo(int contentType, String storeFolderPath, String alarmInfo, byte[] byteArray) {
        String curTimeStampStr = TimeFormatUtil.curTimeFormat();

        // 非二进制类型的数据处理
        if (alarmInfo != null) {
            switch (contentType) {
                case ContentTypeEnum.APPLICATION_JSON:
                case ContentTypeEnum.APPLICATION_XML: {
                    handleAlarmBodyInfo(storeFolderPath, curTimeStampStr, contentType, alarmInfo);
                    break;
                }
            }
        }

        // 二进制的数据信息
        if (byteArray != null) {
            handleByteAryFile(storeFolderPath, curTimeStampStr, contentType, byteArray);
        }
    }

    /**
     * 处理报警事件数据内容
     *
     * @param storeFolderPath
     * @param alarmInfo
     */
    private void handleAlarmBodyInfo(String storeFolderPath, String curTimestamp, int contentType, String alarmInfo) {
        String eventType = "";
        try {
            // TODO 这里需要根据实际的业务逻辑自行处理下报警事件报文结构体类型，根据eventType进行处理
            switch (contentType) {
                case ContentTypeEnum.APPLICATION_JSON: {
                    JSONObject jsonAlarmRecv = new JSONObject(alarmInfo);
                    eventType = jsonAlarmRecv.get("eventType").toString();
                    break;
                }
                case ContentTypeEnum.APPLICATION_XML: {
                    // 注意： 实际设备上报的是xml格式的数据，这里借助工具类转换为Json格式的数据，仅供demo参考用
                    JSONObject xmlObj = XML.toJSONObject(alarmInfo);
                    JSONObject xmlAlarmRecv = (JSONObject) xmlObj.get(xmlObj.keys().next());
                    eventType = xmlAlarmRecv.get("eventType").toString();
                    break;
                }
                default: {
                    System.out.println("未匹配到可以解析的content-type, 请自行处理！");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        String filename = curTimestamp + "_eventType_" + eventType;
        // 保存报警报文内容到文件中
        FileUtil.output2File(storeFolderPath, filename, ContentTypeEnum.getFilePostfix(contentType), alarmInfo);
    }


    /**
     * 处理byte数组到文件
     *
     * @param storeFolderPath
     * @param ch
     */
    private static void handleByteAryFile(String storeFolderPath, String curTimestamp, int contentType, byte[] ch) {
        // 保存byte数组数据到文件中
        FileUtil.byteAry2File(storeFolderPath, curTimestamp, ContentTypeEnum.getFilePostfix(contentType), ch);
    }

}

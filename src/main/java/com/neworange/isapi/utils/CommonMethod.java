package com.neworange.isapi.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author zhengxiaohui
 * @date 2024/1/11 13:58
 * @desc
 */
@Component
public class CommonMethod {

    @Autowired
    private ResourceLoader resourceLoader;

    public static String byteToString(byte[] bytes) {
        if (null == bytes || bytes.length == 0) {
            return "";
        }
        int iLengthOfBytes = 0;
        for (byte st : bytes) {
            if (st != 0) {
                iLengthOfBytes++;
            } else
                break;
        }
        String strContent = "";
        try {
            strContent = new String(bytes, 0, iLengthOfBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return strContent;
    }

    /**
     * utf8字节数组转gbk字节数组
     *
     * @param utf8Bytes
     * @return
     */
    public static byte[] UTF8toGBK(byte[] utf8Bytes) {
        String utf8Str = new String(utf8Bytes, StandardCharsets.UTF_8);
        byte[] gbkBytes = utf8Str.getBytes(Charset.forName("GBK"));
        return gbkBytes;
    }

    /**
     * utf8字节数组转gbk字符串
     *
     * @param utf8Bytes
     * @return
     */
    public static String UTF8toGBKStr(byte[] utf8Bytes) {
        return new String(UTF8toGBK(utf8Bytes), Charset.forName("GBK"));
    }

    /**
     * 获取resource文件夹下的文件绝对路径
     *
     * @param filePath 文件相对于resources文件夹的相对路径, 格式描述举例为 conf/XX/XX.json
     * @return
     */
    public static String getResFileAbsPath(String filePath) {
        if (filePath == null) {
            throw new RuntimeException("filePath null error!");
        }
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:" + filePath);
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException, filePath:" + filePath);
            return null;
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取控制台的一行输入
     *
     * @param inputTip 输入提示
     * @return 控制台的输入信息
     */
    public static String getConsoleInput(String inputTip) {
        System.out.println(inputTip);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    /**
     * char数组转byte数组工具
     *
     * @param chars
     * @return
     */
    public static byte[] getBytes(char[] chars) {
        Charset cs = StandardCharsets.UTF_8;
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }

    /**
     * byte数组转char数组工具类
     *
     * @param bytes
     * @return
     */
    public static char[] getChars(byte[] bytes) {
        Charset cs = StandardCharsets.UTF_8;
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }
}

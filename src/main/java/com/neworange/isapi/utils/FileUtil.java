package com.neworange.isapi.utils;


import com.alibaba.fastjson2.JSONException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author zhengxiaohui
 * @date 2023/12/25 15:45
 * @desc 文件处理工具类
 */
public class FileUtil {

    public static void folderInit(String folder) {
        File directory = new File(folder);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                System.out.println(folder + "_文件夹创建失败！");
            }
        }
    }

    /**
     * 字符串 输出到文件中
     *
     * @param folder      文件存储路径（绝对）
     * @param fileName    文件名
     * @param postFix     文件后缀
     * @param fileContent 文件内容
     */
    public static void output2File(String folder, String fileName, String postFix, String fileContent) {
        folderInit(folder);
        String savePath = folder + fileName + postFix;
        try {
            FileOutputStream fos = new FileOutputStream(savePath);
            fos.write(fileContent.getBytes());
            fos.close();
        } catch (IOException e) {
            System.out.println("输出到文件出现异常：" + e.getMessage());
        }
    }

    /**
     * byte数组 输出到文件中
     *
     * @param folder   文件存储路径（绝对）
     * @param filename 文件名
     * @param postFix  文件后缀
     * @param fileByte 文件内容
     */
    public static void byteAry2File(String folder, String filename, String postFix, byte[] fileByte) {
        folderInit(folder);
        String savePath = folder + filename + postFix;
        try {
            FileOutputStream fos = new FileOutputStream(savePath);
            fos.write(fileByte);
            fos.close();
        } catch (IOException e) {
            System.out.println("输出到文件出现异常：" + e.getMessage());
        }
    }

    /**
     * EN:Save the picture to local
     * CN:保存图片到本地
     *
     * @param storePathPart
     * @param filename
     * @param picByte
     * @throws IOException
     * @throws JSONException
     */
    public static void byteAry2Picture(String storePathPart, String filename, byte[] picByte) throws IOException, JSONException {
        String folder = System.getProperty("user.dir") + storePathPart;
        File directory = new File(folder);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                System.out.println(folder + "_文件夹创建失败！");
            }
        }

        String saveImagePath = folder + filename + ".jpeg";
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(picByte);
            BufferedImage bi1 = ImageIO.read(bais);
            File w2 = new File(saveImagePath);
            if (bi1 != null) {
                ImageIO.write(bi1, "jpg", w2);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

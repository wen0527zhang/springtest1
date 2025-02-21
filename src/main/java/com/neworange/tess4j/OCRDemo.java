package com.neworange.tess4j;

import com.neworange.isapi.utils.CommonMethod;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2025/1/20 15:27
 * @ description
 */
public class OCRDemo {
    public static void main(String[] args) {
        File image =new File("images/img_1.png") ;
        ITesseract instance = new Tesseract();
        instance.setDatapath(CommonMethod.getResFileAbsPath("model/")); // 替换为你的tessdata目录路径
        instance.setLanguage("chi_sim"); // 设置识别语言为简体中文
        try {
            String result = instance.doOCR(image);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }
}

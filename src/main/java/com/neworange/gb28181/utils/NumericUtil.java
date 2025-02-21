package com.neworange.gb28181.utils;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/10 16:22
 * @description
 */
public class NumericUtil {
    /**
     * 判断是否Double格式
     * @param str
     * @return true/false
     */
    public static boolean isDouble(String str) {
        try {
            Double num2 = Double.valueOf(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否Double格式
     * @param str
     * @return true/false
     */
    public static boolean isInteger(String str) {
        try {
            int num2 = Integer.valueOf(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

package com.neworange.utils;

import com.neworange.entity.Detection;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.List;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/5/15 14:55
 * @description
 */
public class ImageUtil {
    /**
     * 调整图像大小并添加边框以适应指定的宽度和高度。
     * @param src 原始图像的 Mat 对象。
     * @param width 目标图像的宽度。
     * @param height 目标图像的高度。
     * @return 调整大小并添加边框后的图像。
     */
    public static Mat resizeWithPadding(Mat src, int width, int height) {
        // 创建一个新的 Mat 对象用于存放结果
        Mat dst = new Mat();
        // 获取原始图像的宽度和高度
        int oldW = src.width();
        int oldH = src.height();
        // 计算缩放比例，取宽度和高度比例的最小值
        double r = Math.min((double) width / oldW, (double) height / oldH);
        // 计算缩放后的图像尺寸，未添加边框
        int newUnpadW = (int) Math.round(oldW * r);
        int newUnpadH = (int) Math.round(oldH * r);
        // 计算需要添加的边框宽度和高度
        int dw = (width - newUnpadW) / 2;
        int dh = (height - newUnpadH) / 2;
        // 计算边框的顶部、底部、左侧和右侧位置
        int top = (int) Math.round(dh - 0.1);
        int bottom = (int) Math.round(dh + 0.1);
        int left = (int) Math.round(dw - 0.1);
        int right = (int) Math.round(dw + 0.1);
        // 先对图像进行缩放
        Imgproc.resize(src, dst, new Size(newUnpadW, newUnpadH));
        // 然后添加边框
        Core.copyMakeBorder(dst, dst, top, bottom, left, right, Core.BORDER_CONSTANT);
        return dst;
    }
    /**
     * 调整图像大小并添加边框以适应指定的宽度和高度，并将结果保存到指定的 Mat 对象。
     * @param src 原始图像的 Mat 对象。
     * @param dst 存放结果的 Mat 对象。
     * @param width 目标图像的宽度。
     * @param height 目标图像的高度。
     */
    public static void resizeWithPadding(Mat src, Mat dst, int width, int height) {
        int oldW = src.width();
        int oldH = src.height();
        double r = Math.min((double) width / oldW, (double) height / oldH);
        int newUnpadW = (int) Math.round(oldW * r);
        int newUnpadH = (int) Math.round(oldH * r);
        int dw = (width - newUnpadW) / 2;
        int dh = (height - newUnpadH) / 2;

        int top = (int) Math.round(dh - 0.1);
        int bottom = (int) Math.round(dh + 0.1);
        int left = (int) Math.round(dw - 0.1);
        int right = (int) Math.round(dw + 0.1);

        Imgproc.resize(src, dst, new Size(newUnpadW, newUnpadH));
        Core.copyMakeBorder(dst, dst, top, bottom, left, right, Core.BORDER_CONSTANT);

    }
    /**
     * 将图像数据的 WHC 格式转换为 CHW 格式。
     * @param src 源数组，WHC 格式。
     * @param dst 目标数组，CHW 格式。
     * @param start 目标数组的起始位置。
     */
    public static void whc2cwh(float[] src, float[] dst, int start) {
        int j = start;
        // 将宽高通道格式转换为通道宽高格式
        for (int ch = 0; ch < 3; ++ch) {
            for (int i = ch; i < src.length; i += 3) {
                dst[j] = src[i];
                j++;
            }
        }
    }

    /**
     * 将边界框的 (x, y, w, h) 格式转换为 (xmin, ymin, xmax, ymax) 格式。
     * @param bbox 原始边界框数组。
     */
    public void xywh2xyxy(float[] bbox) {
        float x = bbox[0];
        float y = bbox[1];
        float w = bbox[2];
        float h = bbox[3];

        bbox[0] = x - w * 0.5f;
        bbox[1] = y - h * 0.5f;
        bbox[2] = x + w * 0.5f;
        bbox[3] = y + h * 0.5f;
    }

    /**
     * 根据缩放比例重新计算边界框的坐标。
     * @param bbox 原始边界框数组。
     * @param orgW 原始图像的宽度。
     * @param orgH 原始图像的高度。
     * @param padW 缩放后图像的宽度。
     * @param padH 缩放后图像的高度。
     * @param gain 缩放比例。
     */
    public void scaleCoords(float[] bbox, float orgW, float orgH, float padW, float padH, float gain) {
        // xmin, ymin, xmax, ymax -> (xmin_org, ymin_org, xmax_org, ymax_org)
        bbox[0] = Math.max(0, Math.min(orgW - 1, (bbox[0] - padW) / gain));
        bbox[1] = Math.max(0, Math.min(orgH - 1, (bbox[1] - padH) / gain));
        bbox[2] = Math.max(0, Math.min(orgW - 1, (bbox[2] - padW) / gain));
        bbox[3] = Math.max(0, Math.min(orgH - 1, (bbox[3] - padH) / gain));
    }
    /**
     * 将图像数据的 WHC 格式转换为 CHW 格式。
     * @param src 源数组，WHC 格式。
     * @return 目标数组，CHW 格式。
     */
    public static float[] whc2cwh(float[] src) {
        float[] chw = new float[src.length];
        int j = 0;
        for (int ch = 0; ch < 3; ++ch) {
            for (int i = ch; i < src.length; i += 3) {
                chw[j] = src[i];
                j++;
            }
        }
        return chw;
    }
    /**
     * 将图像数据的 WHC 格式转换为 CHW 格式，适用于 byte 类型。
     * @param src 源数组，WHC 格式。
     * @return 目标数组，CHW 格式。
     */
    public static byte[] whc2cwh(byte[] src) {
        // 与 float 版本的 whc2cwh 方法类似，但适用于 byte 类型
        byte[] chw = new byte[src.length];
        int j = 0;
        for (int ch = 0; ch < 3; ++ch) {
            for (int i = ch; i < src.length; i += 3) {
                chw[j] = src[i];
                j++;
            }
        }
        return chw;
    }
    /**
     * 在图像上绘制检测结果。
     * @param img 原始图像的 Mat 对象。
     * @param detectionList 包含检测结果的列表。
     */
    public static void drawPredictions(Mat img, List<Detection> detectionList) {
        // debugging image
        // 在图像上绘制每个检测到的边界框和标签
        for (Detection detection : detectionList) {

            float[] bbox = detection.getBbox();
            Scalar color = new Scalar(249, 218, 60);
            Imgproc.rectangle(img, new Point(bbox[0], bbox[1]), new Point(bbox[2], bbox[3]),
                    color, 2
            );
            Imgproc.putText(img, detection.getLabel(),
                    new Point(bbox[0] - 1, bbox[1] - 5),
                    Imgproc.FONT_HERSHEY_SIMPLEX, .5, color, 1);
        }
    }

}

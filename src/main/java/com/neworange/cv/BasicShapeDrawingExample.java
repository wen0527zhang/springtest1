package com.neworange.cv;


import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/6/20 19:05
 * @ description
 */
public class BasicShapeDrawingExample {
    static {
        nu.pattern.OpenCV.loadLocally();
        System.load(ClassLoader.getSystemResource("lib/opencv_java4100.dll").getPath());
    }

    public static void main(String[] args) {
        //通道数 红绿蓝 RGB
        //Mat 通道为BGR
        // 400x600 大小，3 通道的黑色图像
        // dims 维度
        Mat t1=new Mat(400,600, CvType.CV_8UC3,new Scalar(0,0,0));
        //在图像熵绘制基本图像
        Point center=new Point(300,200);//圆心坐标
        Scalar color=new Scalar(0,255,0); //颜色（BGR）
        //绘制圆
        //// 中心为 (300, 200)，半径为 100，边界宽度为 2，绿色
        Imgproc.circle(t1,center,100,color,2);

        // 绘制矩形
        Point topLeft = new Point(100, 100); // 左上角坐标
        Point bottomRight = new Point(500, 300); // 右下角坐标
        Imgproc.rectangle(t1, topLeft, bottomRight, new Scalar(0, 0, 255), 3); // 左上角 (100, 100)，右下角 (500, 300)，红色，边界宽度为 3

        // 绘制直线
        Point start = new Point(50, 50); // 起始点坐标
        Point end = new Point(550, 350); // 结束点坐标
        Imgproc.line(t1, start, end, new Scalar(255, 0, 0), 1); // 蓝色，线宽为 1
        // 在图像上绘制文本
        Point textPosition = new Point(50, 50); // 文本的位置
        String text = "Hello, OpenCV!"; // 要绘制的文本
        // 白色，字体大小为 1.0，线宽为 2
        Imgproc.putText(t1, text, textPosition, Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(255, 255, 255), 2);

        HighGui.imshow("1",t1);
        HighGui.waitKey();
        HighGui.destroyAllWindows();
        System.exit(0);
    }
}

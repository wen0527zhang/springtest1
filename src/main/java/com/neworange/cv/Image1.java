package com.neworange.cv;

import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/6/20 17:35
 * @ description
 */
public class Image1 {
    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        nu.pattern.OpenCV.loadLocally();
//        System.load(ClassLoader.getSystemResource("lib/opencv_java4100.dll").getPath());
    }

    public static void main(String[] args) {
        Mat image = Imgcodecs.imread("images/000000000650.jpg");
        Scalar color = new Scalar(249, 218, 60);
        Imgproc.rectangle(image, new Point(100, 0), new Point(200, 500),
                color, 2
        );
        Imgcodecs.imwrite("images/000000000650.jpg",image);
        System.out.println(image.dims());
        System.out.println(image.depth());
        System.out.println(image.type());
        System.out.println();
//        Mat m1 = image.clone();
//        HighGui.imshow("image", image);
//        int channels=m1.channels();
//        int height= m1.rows();
//        int with=m1.cols();
//        byte [] data=new byte[channels];
//        int b,g,r;
//        for (int rows = 0; rows < height; rows++) {
//            for (int cols=0;cols<with;cols++){
//                m1.get(rows,cols,data);
//                b=data[0];
//                g=data[1];
//                r=data[2];
//                //修改像素
//                b=255-b;
//                g=255-g;
//                r=255-r;
//                data[0]=(byte) b;
//                data[1]=(byte)g;
//                data[2]=(byte)r;
//                m1.put(rows,cols,data);
//            }
//        }
        HighGui.imshow("m1",image);
        HighGui.waitKey();
        HighGui.destroyAllWindows();
    }

/*    public static void main(String[] args) {
        Mat image = Imgcodecs.imread("images/000000000650.jpg");


        Mat image1 = Imgcodecs.imread("images/000000000036.jpg");
        Mat a=new Mat();
        Core.bitwise_not(image1,a);
        HighGui.imshow("a", a);
//        image1.convertTo(image1,0, 0.8, 0);
//        HighGui.imshow("原图", image);
        if (image.empty()){
            System.out.println("Error:Could not load images");
            return;
        }
        // 融合要求两张图片具有相同尺寸
        Size size = getSize(image, image1);
        // 图像融合
        Mat blendedImage = blendImages(resizeImages(image, size), resizeImages(image1, size));
        // 显示融合后的图像
//        HighGui.imshow("Blended Image", blendedImage);
        LinkedHashMap<String, Mat> images = new LinkedHashMap<>();
        images.put("a", a);
        images.put("Blended Image", blendedImage);
        show(images);
        // 定义暗度调整系数（在 0 到 1 之间）
        double darknessFactor = 0.5; // 减少 50%
        // 将图像1的每个像素值乘以暗度调整系数
        // rtype：转换后的目标数据类型。如果值为 -1，则表示使用与源 Mat 对象相同的数据类型。
        // 原像素值*alpha+beta
//        Mat clone = image.clone();
//        clone.convertTo(clone,-1, darknessFactor, 0);

//        HighGui.imshow("明暗变化后", clone);
        HighGui.waitKey(0);
        HighGui.destroyAllWindows();
        System.exit(0);
    }*/

    public static void show(LinkedHashMap<String, Mat> mats) {
        // 获取默认工具包
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        // 获取屏幕的尺寸
        Dimension screenSize = toolkit.getScreenSize();
        // 输出屏幕宽度和高度
        int i = 0;
        int x = 0;
        int y = 0;
        int xSpacing = 10;
        int ySpacing = 40;
        for (Map.Entry<String, Mat> entry : mats.entrySet()) {
            Mat mat = entry.getValue();
            String winName = entry.getKey();
            System.out.println("winName = " + winName);
            HighGui.imshow(winName, mat);
            if (i > 0) {
                x += (mat.cols() + xSpacing);
                if (x + mat.cols() > screenSize.width) {
                    x = 0;
                    y += (mat.rows() + ySpacing);
                }
            }
            HighGui.moveWindow(winName, x, y);
            i++;
        }
        HighGui.waitKey();
    }
    private static Size getSize(Mat image1, Mat image2) {
        int minWidth = Math.min(image1.width(), image2.width());
        int minHeight = Math.min(image1.height(), image2.height());
        return new Size(minWidth, minHeight);
    }

    // 将图像调整为size大小
    private static Mat resizeImages(Mat image, Size size) {
        Mat mat = new Mat(size, image.type());
        Imgproc.resize(image, mat, size);
        return mat;
    }

    // 图像融合
    private static Mat blendImages(Mat image1, Mat image2) {
        double alpha = 0.9; // 调整融合的权重
        Mat blendedImage = new Mat();
        Core.addWeighted(image1, alpha, image2, 1 - alpha, 0, blendedImage);
        return blendedImage;
    }
    public static void  getAddImages(Mat image ){
        //创建与原图1尺寸相同的图像，每个像素为100
        Mat image2=new Mat(image.size(),image.type(),new Scalar(100));
        //创建与原图1尺寸相同的空白图像，用于存储结果
        Mat result=new Mat(image.size(),image.type());
        Mat res2=new Mat(image.size(),image.type());

        Core.add(image,image2,result);

        HighGui.imshow("相加",result);

        //想减
        Core.subtract(image,image2,res2);
        HighGui.imshow("相减",res2);
        HighGui.waitKey();
        HighGui.destroyAllWindows();
        System.exit(0);

    }

    public static void getImage(Mat image) {
        //分离通道
        List<Mat> channels = new ArrayList<>();
        Core.split(image, channels);
        for (int i = 0; i < channels.size(); i++) {
            Mat mat = channels.get(i);
            String name = "channel" + i;
            HighGui.imshow("通道" + name, mat);
        }
        List<Mat> mergeChannels = new ArrayList<>();
        mergeChannels.add(channels.get(0));
        mergeChannels.add(channels.get(1));
        mergeChannels.add(channels.get(2));
        Mat newMat = new Mat();
        Core.merge(mergeChannels, newMat);
        HighGui.imshow("合并后的图像", newMat);
        HighGui.waitKey();
        HighGui.destroyAllWindows();
        System.exit(0);
    }
}

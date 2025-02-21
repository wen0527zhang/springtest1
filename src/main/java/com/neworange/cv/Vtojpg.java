package com.neworange.cv;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.*;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.opencv.core.Point;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/6/20 10:52
 * @description
 */
public class Vtojpg {
    static {
        nu.pattern.OpenCV.loadLocally();
        System.load(ClassLoader.getSystemResource("lib/opencv_java4100.dll").getPath());
    }

    static OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

    public static void main(String[] args) throws Exception {
        Mat imread = Imgcodecs.imread("images/img.png");
        if(null==imread){
            System.out.printf("images is name {}",imread);
        }
        //  是深拷贝
        Mat clone = imread.clone();
        //// 深拷贝
        Mat copyTo = new Mat();
        imread.copyTo(copyTo);

        // 在image上上绘制文本
        // 文本的位置
        Point textPosition =new Point(50,50);
        String text = "Hello, OpenCV!";
        // 白色，字体大小为 1.0，线宽为 2
        Imgproc.putText(imread,text,textPosition,Imgproc.FONT_HERSHEY_SIMPLEX,1.0,new Scalar(255,255,255),2);

        LinkedHashMap<String,Mat> linkedHashMap=new LinkedHashMap<>(3);
        linkedHashMap.put("原图",imread);
        linkedHashMap.put("clone", clone);
        linkedHashMap.put("copyTo", copyTo);
        show(linkedHashMap);
        HighGui.destroyAllWindows();
    }


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

    public  static  void saveVideo(){
        VideoCapture capture = new VideoCapture("video/car3.mp4");
        //获取视频信息
        double width=capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        double height=capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        //总帧数和
        double frameCount=capture.get(Videoio.CAP_PROP_FRAME_COUNT);
        double type=capture.get(Videoio.CAP_PROP_FRAME_TYPE);
        // 获取视频的帧数和总时长
        double totalSeconds = frameCount / capture.get(Videoio.CAP_PROP_FPS);//帧率
        // 计算平均帧率
        double fps = frameCount / totalSeconds;
        // 输出视频信息
        System.out.println("视频宽度: " + width);
        System.out.println("视频高度: " + height);
        System.out.println("视频帧数: " + frameCount);
        System.out.println("总时长: " + totalSeconds);
        System.out.println("平均帧率: " + fps);

        // 定义视频属性 width 和 height 不对写不进去
        Size frameSize = new Size(width, height);
        // 创建 VideoWriter 对象
        VideoWriter videoWriter = new VideoWriter("video/1.mp4", VideoWriter.fourcc('H', '2', '6', '4'), fps, frameSize, true);

        // 检查视频是否成功打开
        if (!capture.isOpened()) {
            System.out.println("无法打开视频");
            return;
        }
        // 检查 VideoWriter 对象是否成功初始化
        if (!videoWriter.isOpened()) {
            System.out.println("错误：无法打开视频文件以进行写入。");
            return;
        }
        // 从视频读取并显示每一帧
        Mat frame = new Mat();
        while (capture.read(frame)){
            if(!frame.empty()){
                videoWriter.write(frame);
            }
            // 显示图片 HighGui用户界面相关
            HighGui.imshow("Video", frame);
            // 输入一次就中断了
            int key = HighGui.waitKey(30);
            System.out.println("key = " + key+"\t" + (char)key);
            if (key == 'q' || key == 'Q') {
                HighGui.destroyAllWindows();
                capture.release();
                videoWriter.release();
                System.exit(0);
                break;
            }
        }
        HighGui.destroyAllWindows();
        capture.release();
        videoWriter.release();
        System.exit(0);
    }
    public static void getCamera(){
        // 打开默认摄像头（索引为0）
        VideoCapture capture = new VideoCapture(0);
        // 检查摄像头是否成功打开
        if (!capture.isOpened()) {
            System.out.println("无法打开摄像头。");
            return;
        }
        // 从摄像头读取并显示每一帧
        Mat frame = new Mat();
        while (capture.read(frame)) {
            // 显示图片 HighGui用户界面相关
            HighGui.imshow("Camera", frame);
            // 等待3毫秒 也就是每3毫秒从摄像头读取一帧
            int key = HighGui.waitKey(3);
            // 如果输入q 则释放资源
            if (key == 'q' || key == 'Q') {
                // 销毁所有窗口
                HighGui.destroyAllWindows();
                // 释放 VideoCapture 对象
                capture.release();
                System.exit(0);
                break;
            }

        }
        // 销毁所有窗口
        HighGui.destroyAllWindows();
        // 释放 VideoCapture 对象
        capture.release();
        System.exit(0);
    }
    public static void getImage1() {
        Mat imread = Imgcodecs.imread("images/img.png");
        HighGui.imshow("hello", imread);
        // 等待按键 不设置设计会一直等待
        HighGui.waitKey();
        // 销毁所有窗口
        HighGui.destroyAllWindows();
        // 退出系统
        System.exit(0);
    }


    public static void getImage() {
        Mat imread = Imgcodecs.imread("images/img.png", -1);
        Frame convert = converter.convert(imread);
        CanvasFrame canvasFrame = new CanvasFrame("test");
        canvasFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        canvasFrame.showImage(convert);
    }

    public static void getVideo() {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        try {
            grabber.start();
            CanvasFrame canvasFrame = new CanvasFrame("test");
            canvasFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            while (canvasFrame.isDisplayable()) {
                Frame frame = grabber.grab();
//            OpenCVFrameConverter converter = new OpenCVFrameConverter.ToMat();
                //converter.convertToOrgOpenCvCoreMat(frame);
//            Mat mat = converter.convertToOrgOpenCvCoreMat(frame);
//            MatOfInt matOfInt = new MatOfInt(Imgcodecs.IMWRITE_JPEG_CHROMA_QUALITY,80);
//            boolean imwrite = Imgcodecs.imwrite("a.png", mat);
                canvasFrame.showImage(frame);
            }

        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                grabber.close();
            } catch (FrameGrabber.Exception e) {
                throw new RuntimeException(e);
            }
        }


    }
}

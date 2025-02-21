package com.neworange.utils;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.nio.FloatBuffer;
import java.util.HashMap;
// 低效率ffmpeg示例，只提供思路，
// 播放工具flv
public class RTSPStreamer {
    static Mat img = null;

    public static void main(String[] args) throws OrtException, FrameRecorder.Exception {

        nu.pattern.OpenCV.loadLocally();
        img= new Mat();

        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("win")) {
            System.load(ClassLoader.getSystemResource("lib/opencv_videoio_ffmpeg4100_64.dll").getPath());
        }
        String model_path = "src\\main\\resources\\model\\yolov7-tiny.onnx";

        String[] labels = {
                "person", "bicycle", "car", "motorcycle", "airplane", "bus", "train",
                "truck", "boat", "traffic light", "fire hydrant", "stop sign", "parking meter",
                "bench", "bird", "cat", "dog", "horse", "sheep", "cow", "elephant", "bear",
                "zebra", "giraffe", "backpack", "umbrella", "handbag", "tie", "suitcase",
                "frisbee", "skis", "snowboard", "sports ball", "kite", "baseball bat",
                "baseball glove", "skateboard", "surfboard", "tennis racket", "bottle",
                "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple",
                "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut",
                "cake", "chair", "couch", "potted plant", "bed", "dining table", "toilet",
                "tv", "laptop", "mouse", "remote", "keyboard", "cell phone", "microwave",
                "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "scissors",
                "teddy bear", "hair drier", "toothbrush"};

        // 加载ONNX模型
        OrtEnvironment environment = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();

        //sessionOptions.addCUDA(0);

        OrtSession session = environment.createSession(model_path, sessionOptions);
        // 输出基本信息
        session.getInputInfo().keySet().forEach(x -> {
            try {
                System.out.println("input name = " + x);
                System.out.println(session.getInputInfo().get(x).getInfo().toString());
            } catch (OrtException e) {
                throw new RuntimeException(e);
            }
        });

        ODConfig odConfig = new ODConfig();
        VideoCapture video = new VideoCapture();
        video.open(0);  //获取电脑上第0个摄像头
        if (!video.isOpened()) {
            video.open("video/car3.mp4");
        }
        int videoWidth = (int)video.get(Videoio.CAP_PROP_FRAME_WIDTH);
        int heightWidth = (int)video.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        int frameRate = (int)video.get(Videoio.CAP_PROP_FPS);
        int minDwDh = Math.min(videoWidth, heightWidth);
        int thickness = minDwDh / ODConfig.lineThicknessRatio;
        int detect_skip = 4;
        // 跳帧计数
        int detect_skip_index = 1;
        // 最新一帧也就是上一帧推理结果
        float[][] outputData   = null;
        //当前最新一帧。上一帧也可以暂存一下
        Mat image;
        Letterbox letterbox = new Letterbox();
        OnnxTensor tensor;
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                // 视频720分辨率最好，20-25帧最佳 , 画面清晰度自己调参ffmpeg很多参数
                FrameRecorder recorder = new FFmpegFrameRecorder("rtmp://192.168.167.141:30780/live/hiknvr-86", videoWidth, heightWidth, 0);
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                recorder.setFormat("flv");
                recorder.setFrameRate(frameRate);
                recorder.setVideoBitrate(1024); //画面清晰的和模糊和马赛克 受码率影响，码率越大画面越清晰，延迟越高，根据自己服务器带宽调整
                recorder.setVideoOption("tune", "zerolatency");
                recorder.setVideoOption("preset", "ultrafast");
                recorder.setOption("buffer_size", "1000k");
                recorder.setOption("max_delay", "500000");
                recorder.setOption("rtmp_buffer", "100");
                recorder.setOption("rtmp_live", "live");
                recorder.setGopSize(50);
                try {
                    recorder.start();
                } catch (FrameRecorder.Exception e) {
                    throw new RuntimeException(e);
                }
                OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
                while(true){
                    Frame frame = converterToMat.convert(img.clone());
                    try {
                        Thread.sleep((long)(1000/frameRate));
                        recorder.record(frame);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }


            }
        });
        // 启动线程
        t.start();
        // 使用多线程和GPU可以提升帧率，一个线程拉流，一个线程模型推理，中间通过变量或者队列交换数据,代码示例仅仅使用单线程
        while (video.read(img)) {
            if ((detect_skip_index % detect_skip == 0) || outputData == null){
                image = img.clone();
                image = letterbox.letterbox(image);
                Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGB);

                image.convertTo(image, CvType.CV_32FC1, 1. / 255);
                float[] whc = new float[3 * 640 * 640];
                image.get(0, 0, whc);
                float[] chw = ImageUtil.whc2cwh(whc);

                detect_skip_index = 1;

                FloatBuffer inputBuffer = FloatBuffer.wrap(chw);
                tensor = OnnxTensor.createTensor(environment, inputBuffer, new long[]{1, 3, 640, 640});

                HashMap<String, OnnxTensor> stringOnnxTensorHashMap = new HashMap<>();
                stringOnnxTensorHashMap.put(session.getInputInfo().keySet().iterator().next(), tensor);
                OrtSession.Result output = session.run(stringOnnxTensorHashMap);

                // 得到结果,缓存结果
                outputData = (float[][]) output.get(0).getValue();
            }else{
                detect_skip_index = detect_skip_index + 1;
            }

            for(float[] x : outputData){

                Point topLeft = new Point((x[1] - letterbox.getDw()) / letterbox.getRatio(), (x[2] - letterbox.getDh()) / letterbox.getRatio());
                Point bottomRight = new Point((x[3] - letterbox.getDw()) / letterbox.getRatio(), (x[4] - letterbox.getDh()) / letterbox.getRatio());
                Scalar color = new Scalar(odConfig.getOtherColor((int) x[0]));

                Imgproc.rectangle(img, topLeft, bottomRight, color, thickness);
                // 框上写文字
                // String boxName = labels[(int) x[0]];
                // Point boxNameLoc = new Point((x[1] - letterbox.getDw()) / letterbox.getRatio(), (x[2] - letterbox.getDh()) / letterbox.getRatio() - 3);
                // Imgproc.putText(img, boxName, boxNameLoc, fontFace, 0.7, color, thickness);

            }
        }
        video.release();
        System.exit(0);

    }
}

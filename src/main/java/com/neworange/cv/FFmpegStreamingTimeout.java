package com.neworange.cv;

import com.neworange.entity.VideoInfo;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/6/25 13:50
 * @ description
 */
public class FFmpegStreamingTimeout {
    /**
     * There is no universal option for streaming timeout. Each of protocols has
     * its own list of options.
     */
    private static enum TimeoutOption {
        /**
         * Depends on protocol (FTP, HTTP, RTMP, RTSP, SMB, SSH, TCP, UDP, or UNIX).
         * http://ffmpeg.org/ffmpeg-all.html
         *
         * Specific for RTSP:
         * Set socket TCP I/O timeout in microseconds.
         * http://ffmpeg.org/ffmpeg-all.html#rtsp
         */
        TIMEOUT,
        /**
         * Protocols
         *
         * Maximum time to wait for (network) read/write operations to complete,
         * in microseconds.
         *
         * http://ffmpeg.org/ffmpeg-all.html#Protocols
         */
        RW_TIMEOUT;

        public String getKey() {
            return toString().toLowerCase();
        }

    }

    private static final String SOURCE_RTSP = "rtsp://admin:xincheng191213@172.16.100.4:554/h264/ch1/main/av_stream";
    private static final int TIMEOUT = 10; // In seconds.

    public static void main(String[] args) {
        rtspStreamingTest();
//        testWithCallback(); // http://172.16.100.4/
    }

    private static void rtspStreamingTest() {
        //String SOURCE_RTSP="video/car3.mp4";
        VideoInfo info = new VideoInfo();
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(SOURCE_RTSP)) {
            /**
             * "rw_timeout" - IS IGNORED when a network cable have been
             * unplugged before a connection but the option takes effect after a
             * connection was established.
             *
             * "timeout" - works fine.
             */
//            grabber.setOption(TimeoutOption.TIMEOUT.getKey(), String.valueOf(TIMEOUT * 1000000)); // In microseconds.

            grabber.start();

            Frame frame = null;
            int lengthInFrames = grabber.getLengthInVideoFrames();
            double frameRate = grabber.getVideoFrameRate();
            double duration = grabber.getLengthInTime() / 1000000.00;
            int width = grabber.getImageWidth();
            int height = grabber.getImageHeight();
            int audioChannel = grabber.getAudioChannels();
            String videoCode = grabber.getVideoCodecName();
            String audioCode = grabber.getAudioCodecName();

            int sampleRate = grabber.getSampleRate();

            info.setLengthInFrames(lengthInFrames);
            info.setFrameRate(frameRate);
            info.setDuration(duration);
            info.setWidth(width);
            info.setHeight(height);
            info.setAudioChannel(audioChannel);
            info.setVideoCode(videoCode);
            info.setAudioCode(audioCode);
            info.setSampleRate(sampleRate);
            System.out.println("info " + info.toString());
             int num=0;
            /**
             * When network is disabled (before grabber was started) grabber
             * throws exception: "org.bytedeco.javacv.FrameGrabber$Exception:
             * avformat_open_input() error -138: Could not open input...".
             *
             * When connections is lost (after a few grabbed frames)
             * grabber.grab() returns null without exception.
             */
            while ((frame = grabber.grab()) != null) {
//                canvasFrame.showImage(frame);
                Frame frame1 = grabber.grabImage();
//                int width=   frame1.imageWidth;
//                int heigth=frame1.imageHeight;
//
                //获取视频解码后的图像像素，也就是说这时的Frame中的opaque存放的是AVFrame

               if(null!=frame1){

                   Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                   BufferedImage bi = java2DFrameConverter.getBufferedImage(frame1);
                   File file = null;
                   try {
                        file=new File("a"+File.separator+num+".png");
                       if(null!=bi){
                          // ImageIO.write(bi, "png", file);
                       }
                       System.out.println();
                   } catch (Exception e) {
                       throw new RuntimeException(e);
                   }finally {
                       frame1.close();
                   }
//                   imwrite(Instant.now().toString()+".png", img);
               }
                num++;
               //imshow("face_recognizer", videoMat);

            }
            grabber.stop();
//            canvasFrame.dispose();
            System.out.println("loop end with frame: " + frame);
        } catch (FrameGrabber.Exception ex) {
            System.out.println("exception: " + ex);
        }finally {

        }
        System.out.println("end");
    }


}

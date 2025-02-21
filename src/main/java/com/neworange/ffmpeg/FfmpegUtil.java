package com.neworange.ffmpeg;

import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.process.ProcessWrapper;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;

import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2024/12/31 11:35
 * @ description
 */
public class FfmpegUtil {
    /**
     * 获取音视频时长
     *
     * @param sourcePath
     * @return
     * @throws EncoderException
     */
    public static long getFileDuration(String sourcePath) throws EncoderException {
        MultimediaObject multimediaObject = new MultimediaObject(new File(sourcePath));
        MultimediaInfo multimediaInfo = multimediaObject.getInfo();
        return multimediaInfo.getDuration();
    }
    /**
     * 通过本地路径获取多媒体文件信息(宽，高，时长，编码等)
     *
     * @param localPath 本地路径
     * @return MultimediaInfo 对象,包含 (宽，高，时长，编码等)
     * @throws EncoderException
     */
    public static MultimediaInfo getMultimediaInfo(String localPath) {
        MultimediaInfo multimediaInfo = null;
        try {
            multimediaInfo = new MultimediaObject(new File(localPath)).getInfo();
        } catch (EncoderException e) {
            System.out.println("获取多媒体文件信息异常");
            e.printStackTrace();
        }
        return multimediaInfo;
    }

    /**
     * 通过URL获取多媒体文件信息
     *
     * @param url 网络url
     * @return MultimediaInfo 对象,包含 (宽，高，时长，编码等)
     * @throws EncoderException
     */
    public static MultimediaInfo getMultimediaInfoFromUrl(String url) {
        MultimediaInfo multimediaInfo = null;
        try {
            multimediaInfo = new MultimediaObject(new URL(url)).getInfo();
        } catch (Exception e) {
            System.out.println("获取多媒体文件信息异常");
            e.printStackTrace();
        }
        return multimediaInfo;
    }

    private static final int SAMPLING_RATE = 16000;
    private static final int SINGLE_CHANNEL = 1;

    /**
     * 音频格式化为wav,并设置单声道和采样率
     *
     * @param url 需要转格式的音频
     * @param targetPath 格式化后要保存的目标路径
     */
    public static boolean formatAudio(String url, String targetPath) {
        File target = new File(targetPath);
        MultimediaObject multimediaObject;
        try {
            // 若是本地文件： multimediaObject = new MultimediaObject(new File("你的本地路径"));
            multimediaObject = new MultimediaObject(new URL(url));
            // 音频参数
            // TODO: 2023/1/31 此处按需自定义音频参数
            AudioAttributes audio = new AudioAttributes();
            // 采样率
            audio.setSamplingRate(SAMPLING_RATE);
            // 单声道
            audio.setChannels(SINGLE_CHANNEL);
            Encoder encoder = new Encoder();
            EncodingAttributes attrs = new EncodingAttributes();
            // 输出格式
            attrs.setOutputFormat("wav");
            attrs.setAudioAttributes(audio);
            encoder.encode(multimediaObject, target, attrs);
            return true;
        } catch (Exception e) {
            System.out.println("格式化音频异常");
            return false;
        }
    }
    /**
     * 合并两个视频
     *
     * @param firstFragmentPath   资源本地路径或者url
     * @param secondFragmentPath  资源本地路径或者url**
     * @param targetPath     目标存储位置
     * @throws Exception
     */
    public static void mergeAv(String firstFragmentPath, String secondFragmentPath,
                               String targetPath) {
        try {
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(firstFragmentPath);
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(secondFragmentPath);
            ffmpeg.addArgument("-filter_complex");
            ffmpeg.addArgument(
                    "\"[0:v] [0:a] [1:v] [1:a] concat=n=2:v=1:a=1 [v] [a]\" -map \"[v]\" -map \"[a]\"");
            ffmpeg.addArgument(targetPath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 视频格式化为mp4
     *
     * @param url
     * @param targetPath
     * @return
     */
    public static boolean formatToMp4(String url, String targetPath) {
        File target = new File(targetPath);
        MultimediaObject multimediaObject;
        try {
            // 若是本地文件： multimediaObject = new MultimediaObject(new File("你的本地路径"));
            multimediaObject = new MultimediaObject(new URL(url));
            EncodingAttributes attributes = new EncodingAttributes();
            // 设置视频的音频参数
            AudioAttributes audioAttributes = new AudioAttributes();
            attributes.setAudioAttributes(audioAttributes);
            // 设置视频的视频参数
            VideoAttributes videoAttributes = new VideoAttributes();
            // 设置帧率
            videoAttributes.setFrameRate(25);
            attributes.setVideoAttributes(videoAttributes);
            // 设置输出格式
            attributes.setOutputFormat("mp4");
            Encoder encoder = new Encoder();
            encoder.encode(multimediaObject, target, attributes);
            return true;
        } catch (Exception e) {
            System.out.println("格式化视频异常");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取视频缩略图 获取视频第0秒的第一帧图片
     *
     * <p>执行的ffmpeg 命令为： ffmpeg -i 你的视频文件路径 -ss 指定的秒数 生成文件的全路径地址
     *
     * @param localPath 本地路径
     * @param targetPath 存放的目标路径
     * @return
     */
    public static boolean getTargetThumbnail(String localPath, String targetPath) {
        // FIXME: 2023/1/31  该方法基本可作为执行ffmpeg命令的模板方法，之后的几个方法与此类似
        try {
            try(ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();){
                ffmpeg.addArgument("-i");
                ffmpeg.addArgument(localPath);
                ffmpeg.addArgument("-ss");
                // 此处可自定义视频的秒数
                ffmpeg.addArgument("0");
                ffmpeg.addArgument(targetPath);
                ffmpeg.execute();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                    blockFfmpeg(br);
                }}
        } catch (IOException e) {
            System.out.println("获取视频缩略图失败");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 等待命令执行成功，退出
     *
     * @param br
     * @throws IOException
     */
    private static void blockFfmpeg(BufferedReader br) throws IOException {
        String line;
        // 该方法阻塞线程，直至合成成功
        while ((line = br.readLine()) != null) {
            doNothing(line);
        }
    }

    /**
     * 打印日志
     *
     * @param line
     */
    private static void doNothing(String line) {
        // FIXME: 2023/1/31 正式使用时注释掉此行，仅用于观察日志
        System.out.println(line);
    }

}

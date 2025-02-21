package com.neworange.vosk;


import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.info.AudioInfo;
import ws.schild.jave.info.MultimediaInfo;

import java.io.File;

public class Jave2Util {

    /**
     * @param src      来源文件路径
     * @param target   目标文件路径
     * @param offset   设置起始偏移量(秒)
     * @param duration 设置切片的音频长度(秒)
     * @throws EncoderException
     */
    public static void cut(String src, String target, Float offset, Float duration) throws EncoderException, ws.schild.jave.EncoderException {

        File targetFile = new File(target);
        if (targetFile.exists()) {
            targetFile.delete();
        }

        File srcFile = new File(src);
        MultimediaObject srcMultiObj = new MultimediaObject(srcFile);
        MultimediaInfo srcMediaInfo = srcMultiObj.getInfo();

        Encoder encoder = new Encoder();

        EncodingAttributes encodingAttributes = new EncodingAttributes();
        //设置起始偏移量(秒)
        encodingAttributes.setOffset(offset);
        //设置切片的音频长度(秒)
        encodingAttributes.setDuration(duration);
        // 输入格式
        encodingAttributes.setInputFormat("wav");

        //设置音频属性
        AudioAttributes audio = new AudioAttributes();
        audio.setBitRate(srcMediaInfo.getAudio().getBitRate());
        //audio.setSamplingRate(srcMediaInfo.getAudio().getSamplingRate());
        // 转换为16KHZ 满足vosk识别的标准
        audio.setSamplingRate(16000);
        audio.setChannels(srcMediaInfo.getAudio().getChannels());
        //如果截取的时候，希望同步调整编码，可以设置不同的编码
//        audio.setCodec("pcm_u8");
        //audio.setCodec(srcMediaInfo.getAudio().getDecoder().split(" ")[0]);
        encodingAttributes.setAudioAttributes(audio);
        //写文件
        encoder.encode(srcMultiObj, new File(target), encodingAttributes);
    }

    /**
     * 转化音频格式
     *
     * @param oldFormatPath : 原音乐路径
     * @param newFormatPath : 目标音乐路径
     * @return
     */
    public static boolean transforMusicFormat(String oldFormatPath, String newFormatPath) {
        File source = new File(oldFormatPath);
        File target = new File(newFormatPath);
        // 音频转换格式类
        Encoder encoder = new Encoder();
        // 设置音频属性
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec(null);
        // 设置转码属性
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setInputFormat("wav");
        attrs.setAudioAttributes(audio);
        try {
            encoder.encode(new MultimediaObject(source), target, attrs);
            System.out.println("传唤已完成...");
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InputFormatException e) {
            e.printStackTrace();
        } catch (EncoderException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void main(String[] args) throws EncoderException, ws.schild.jave.EncoderException {

        String src = "D:\\fjFile\\annex\\xwbl\\ly8603f22f24e0409fa9747d50a78ff7e5.wav";
        String target = "D:\\fjFile\\annex\\xwbl\\tem_2.wav";

        Jave2Util.cut(src, target, 0.0F, 60.0F);

        String inputFormatPath = "D:\\fjFile\\annex\\xwbl\\ly8603f22f24e0409fa9747d50a78ff7e5.m4a";
        String outputFormatPath = "D:\\fjFile\\annex\\xwbl\\ly8603f22f24e0409fa9747d50a78ff7e5.wav";

        info(inputFormatPath);

        // audioEncode(inputFormatPath, outputFormatPath);


    }

    /**
     * 获取音频文件的编码信息
     *
     * @param filePath
     * @throws EncoderException
     */
    private static void info(String filePath) throws EncoderException, ws.schild.jave.EncoderException {
        File file = new File(filePath);
        MultimediaObject multimediaObject = new MultimediaObject(file);
        MultimediaInfo info = multimediaObject.getInfo();
        // 时长
        long duration = info.getDuration();
        String format = info.getFormat();
        // format:mov
        System.out.println("format:" + format);
        AudioInfo audio = info.getAudio();
        // 它设置将在重新编码的音频流中使用的音频通道数（1 =单声道，2 =立体声）。如果未设置任何通道值，则编码器将选择默认值。
        int channels = audio.getChannels();
        // 它为新的重新编码的音频流设置比特率值。如果未设置比特率值，则编码器将选择默认值。
        // 该值应以每秒位数表示。例如，如果您想要128 kb / s的比特率，则应调用setBitRate（new Integer（128000））。
        int bitRate = audio.getBitRate();
        // 它为新的重新编码的音频流设置采样率。如果未设置采样率值，则编码器将选择默认值。该值应以赫兹表示。例如，如果您想要类似CD
        // 采样率、音频采样级别 16000 = 16KHz
        int samplingRate = audio.getSamplingRate();

        // 设置音频音量
        // 可以调用此方法来更改音频流的音量。值为256表示音量不变。因此，小于256的值表示音量减小，而大于256的值将增大音频流的音量。
        // setVolume(Integer volume)

        String decoder = audio.getDecoder();

        System.out.println("声音时长:毫秒" + duration);
        System.out.println("声道:" + channels);
        System.out.println("bitRate:" + bitRate);
        System.out.println("samplingRate 采样率、音频采样级别 16000 = 16KHz:" + samplingRate);
        // aac (LC) (mp4a / 0x6134706D)
        System.out.println("decoder:" + decoder);
    }

    /**
     * 音频格式转换
     *
     * @param inputFormatPath
     * @param outputFormatPath
     * @return
     */
    public static boolean audioEncode(String inputFormatPath, String outputFormatPath) {
        String outputFormat = getSuffix(outputFormatPath);
        String inputFormat = getSuffix(inputFormatPath);
        File source = new File(inputFormatPath);
        File target = new File(outputFormatPath);
        try {
            MultimediaObject multimediaObject = new MultimediaObject(source);
            // 获取音频文件的编码信息
            MultimediaInfo info = multimediaObject.getInfo();
            AudioInfo audioInfo = info.getAudio();
            //设置音频属性
            AudioAttributes audio = new AudioAttributes();
            audio.setBitRate(audioInfo.getBitRate());
            audio.setSamplingRate(audioInfo.getSamplingRate());
            audio.setChannels(audioInfo.getChannels());
            // 设置转码属性
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setInputFormat(inputFormat);
            attrs.setOutputFormat(outputFormat);
            attrs.setAudioAttributes(audio);
            // 音频转换格式类
            Encoder encoder = new Encoder();
            // 进行转换
            encoder.encode(new MultimediaObject(source), target, attrs);
            return true;
        } catch (IllegalArgumentException | EncoderException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * 获取文件路径的.后缀
     *
     * @param outputFormatPath
     * @return
     */
    private static String getSuffix(String outputFormatPath) {
        return outputFormatPath.substring(outputFormatPath.lastIndexOf(".") + 1);
    }

}


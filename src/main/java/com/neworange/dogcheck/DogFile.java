package com.neworange.dogcheck;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * @author Jun
 * @date 2023/4/10&17:39
 */
@Component
@Slf4j
public class DogFile {
    static {
        System.load(ClassLoader.getSystemResource("dog/libRY1Java.so").getPath());
    }

    private String _pid = "EBFA6BCD";

    //	@Value("${dog.pass}")
    private String _userpin = "ffffffffffffxczh";

    private IRY1 ry = new CRY1();

    private volatile boolean isOpen = false;

    private char[] charPid = new char[16]; // 16
    private char[] charUserPin = new char[32]; // 24

    // 默认打开设备重试次数
    private static final int DEF_RETRIES = 3;


    @PostConstruct
    public void init() {
        int j = 0, i = 0;
        for (i = 0; i < _pid.length(); i++) {
            charPid[i] = _pid.charAt(i);
        }

        for (j = 0; j < _userpin.length(); j++) {
            charUserPin[j] = _userpin.charAt(j);
        }

    }

    public boolean openDevice() {
        try {
            return doOpenDevice();
        } catch (Exception e) {
            log.error("open device err:" + e.getMessage(), e);
            return false;
        }
    }


    private boolean doOpenDevice() throws Exception {
        int[] Count = new int[1];
        log.debug("Try to Find Rockey1...");
        ry.RY1_Find(charPid, Count);
        //
        if (Count[0] != 0) {
            log.debug("Find Rockey1!");
            // ===============
            log.debug("Try to open Rockey1...");
            ry.RY1_Open(charPid, 0);
            log.debug("Open a Rockey1 success!");
            isOpen = true;
            return true;
        } else {
            log.debug("No Rockey1 device were found!");
            return false;
        }
    }

    public void validateUser() {
        char[] RemainCount = new char[1];
        log.debug("Verify DeveloperPIN:" + String.valueOf(charPid));
        ry.RY1_VerifyUserPin(charUserPin, RemainCount);
        log.debug("Verify USERPIN success! RemainCount=" + String.valueOf(RemainCount));
    }

    public String readPri() {
        return read(false);
    }

    private String read(boolean isPub) {
        log.debug("RY1_Read...");
        short[] memlength = new short[1];
        memlength[0] = 128;
        char[] tmpbuf = new char[128];
        ry.RY1_Read((short) 0, (short) 128, tmpbuf, memlength, (char) (isPub ? 0 : 1));
        log.debug("RY1_Read success!" + new String(tmpbuf));
        return new String(tmpbuf);
    }

    public String rockeyRsaDecode(String enc) {
        try {
            if (enc != null && !enc.isEmpty()) {
                char[] data = getChars(hexString2Bytes(enc));
                char[] ndata = new char[128];
                ndata[0] = (char) 0;
                ndata[1] = (char) 0;
                System.arraycopy(data, 0, ndata, 2, data.length);
                for (int i = data.length + 2; i < 128; i++) {
                    ndata[i] = (char) i;
                }
                ry.RY1_RSADec((char) 1, (char) 0, ndata, ndata.length);
                return new String(data);
            }
        } catch (Exception e) {
            log.error("dog ras decode err:" + e.getMessage(), e);
        }
        return "";
    }

    public String rockeyDesDecode(String enc) {
        try {
            if (enc != null && !enc.isEmpty()) {
                char[] data = getChars(hexString2Bytes(enc));
                char[] ndata = new char[128];
                System.arraycopy(data, 0, ndata, 0, data.length);
                for (int i = data.length; i < 128; i++) {
                    ndata[i] = (char) i;
                }
                ry.RY1_TDesDec((char) 0, ndata, ndata.length);
                return new String(data);
            }
        } catch (Throwable e) {
            log.error("dog des decode err:" + e.getMessage(), e);
        }
        return "";
    }

    private byte[] hexString2Bytes(String src) {
        byte[] ret = new byte[32];
        byte[] tmp = src.getBytes();
        //
        for (int i = 0; i < 32; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    private byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }

    public String getHid() {
        char[] newP = new char[16];
        ry.RY1_GetHID(newP);
        return new String(newP);
    }

//    public static String getCode(String time) {
//        return RsaCrypt.getEncSecVal(new String(SysUtils.makeMarchinCode()), time);
//    }

    public boolean close() {
        //System.out.println("Close Rockey1...");
        if (ry != null && isOpen) {
            try {
                ry.RY1_Close();
            } catch (Throwable e) {
                e.printStackTrace();
                return false;
            } finally {
                isOpen = false;
            }
        }
        //System.out.println("Close Rockey1 success!");
        return true;
    }

//    public String getId() {
//        String flag = null;
//        Future<String> future = pools.submit(new DogHidCallable());
//        try {
//            flag = future.get(2, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            logger.error("dog hid callable err:" + e.getMessage(), e);
//            logger.error("timeout执行线程的状态：" + future.isDone());
//            future.cancel(true);
//            logger.error("timeout再看执行线程的状态：" + future.isDone());
//        }
//        return flag;
//    }

    public static void main(String[] args) {

        DogFile dog = new DogFile();

        System.out.println(dog.getHid());

    }
}

package com.neworange.dogcheck;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2024/8/8 19:12
 * @ description
 */
public class RY1API {
    private long hCtx;
    private int bInit = 0;

    static {
        System.loadLibrary("RY1Java");
    }

    public RY1API() {
        this.LoadLibrary();
    }

    public void finalize() {
        if (this.bInit == 1) {
            this.bInit = 0;
            this.FreeLibrary();
        }

    }

    public void Thrown1(int var1, String var2) {
        throw new RTException(var1, var2);
    }

    public void Thrown2(int var1) {
        throw new RTException(var1);
    }

    public native void Find(char[] var1, int[] var2);

    public native void Open(char[] var1, int var2);

    public native void Close();

    public native void GetVersion(char[] var1, char[] var2);

    public native void LEDControl(char var1);

    public native void ProducePID(char[] var1, char var2, char[] var3);

    public native void GetHID(char[] var1);

    public native void GetPID(char[] var1);

    public native void VerifyUserPin(char[] var1, char[] var2);

    public native void ChangeUserPin(char[] var1, char[] var2);

    public native void ResetUserPin();

    public native void SetTryCountForUserPin(char var1);

    public native void VerifySoPin(char[] var1, char[] var2);

    public native void ProduceSoPin(char[] var1, char var2, char[] var3);

    public native void GenRandom(char[] var1);

    public native void SetTryCountForSoPin(char var1);

    public native void Read(short var1, short var2, char[] var3, short[] var4, char var5);

    public native void Write(short var1, short var2, char[] var3, short[] var4, char var5);

    public native void ResetSecurityState();

    public native void SetTDesKey(char var1, char[] var2);

    public native void TDesEnc(char var1, char[] var2, int var3);

    public native void TDesDec(char var1, char[] var2, int var3);

    public native void GenRSAKey(char var1, char var2, byte[] var3, byte[] var4);

    public native void SetRSAKey(char var1, char var2, byte[] var3, byte[] var4);

    public native void RSAEnc(char var1, char var2, char[] var3, int var4);

    public native void RSADec(char var1, char var2, char[] var3, int var4);

    public native void SetCounter(char var1);

    public native void GetCounter(char[] var1);

    public native void SetUpdatePacket(int var1, short var2, short var3, char[] var4, int var5);

    public native void GenUpdatePacket(byte[] var1, char[] var2, short[] var3);

    public native void Update(char[] var1, short var2);

    public native void GetErrInfo(int var1);

    private native void LoadLibrary();

    private native void FreeLibrary();
}

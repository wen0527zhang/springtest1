package com.neworange.dogcheck;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2024/8/8 19:10
 * @ description
 */
public interface IRY1 {
    void RY1_Find(char[] var1, int[] var2);

    void RY1_Open(char[] var1, int var2);

    void RY1_Close();

    void RY1_GetVersion(char[] var1, char[] var2);

    void RY1_LEDControl(char var1);

    void RY1_ProducePID(char[] var1, char var2, char[] var3);

    void RY1_GetHID(char[] var1);

    void RY1_GetPID(char[] var1);

    void RY1_VerifyUserPin(char[] var1, char[] var2);

    void RY1_ChangeUserPin(char[] var1, char[] var2);

    void RY1_ResetUserPin();

    void RY1_SetTryCountForUserPin(char var1);

    void RY1_VerifySoPin(char[] var1, char[] var2);

    void RY1_ProduceSoPin(char[] var1, char var2, char[] var3);

    void RY1_GenRandom(char[] var1);

    void RY1_SetTryCountForSoPin(char var1);

    void RY1_Read(short var1, short var2, char[] var3, short[] var4, char var5);

    void RY1_Write(short var1, short var2, char[] var3, short[] var4, char var5);

    void RY1_ResetSecurityState();

    void RY1_SetTDesKey(char var1, char[] var2);

    void RY1_TDesEnc(char var1, char[] var2, int var3);

    void RY1_TDesDec(char var1, char[] var2, int var3);

    void RY1_GenRSAKey(char var1, char var2, byte[] var3, byte[] var4);

    void RY1_SetRSAKey(char var1, char var2, byte[] var3, byte[] var4);

    void RY1_RSAEnc(char var1, char var2, char[] var3, int var4);

    void RY1_RSADec(char var1, char var2, char[] var3, int var4);

    void RY1_SetCounter(char var1);

    void RY1_GetCounter(char[] var1);

    void RY1_SetUpdatePacket(int var1, short var2, short var3, char[] var4, int var5);

    void RY1_GenUpdatePacket(byte[] var1, char[] var2, short[] var3);

    void RY1_Update(char[] var1, short var2);

    void RY1_GetErrInfo(int var1);
}

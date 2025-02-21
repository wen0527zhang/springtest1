package com.neworange.dogcheck;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2024/8/8 19:11
 * @ description
 */
public class CRY1 implements IRY1{
    private RY1API RY1 = new RY1API();

    public CRY1() {
    }

    public void RY1_Find(char[] var1, int[] var2) {
        this.RY1.Find(var1, var2);
    }

    public void RY1_Open(char[] var1, int var2) {
        this.RY1.Open(var1, var2);
    }

    public void RY1_Close() {
        this.RY1.Close();
    }

    public void RY1_GetVersion(char[] var1, char[] var2) {
        this.RY1.GetVersion(var1, var2);
    }

    public void RY1_LEDControl(char var1) {
        this.RY1.LEDControl(var1);
    }

    public void RY1_ProducePID(char[] var1, char var2, char[] var3) {
        this.RY1.ProducePID(var1, var2, var3);
    }

    public void RY1_GetHID(char[] var1) {
        this.RY1.GetHID(var1);
    }

    public void RY1_GetPID(char[] var1) {
        this.RY1.GetPID(var1);
    }

    public void RY1_VerifyUserPin(char[] var1, char[] var2) {
        this.RY1.VerifyUserPin(var1, var2);
    }

    public void RY1_ChangeUserPin(char[] var1, char[] var2) {
        this.RY1.ChangeUserPin(var1, var2);
    }

    public void RY1_ResetUserPin() {
        this.RY1.ResetUserPin();
    }

    public void RY1_SetTryCountForUserPin(char var1) {
        this.RY1.SetTryCountForUserPin(var1);
    }

    public void RY1_VerifySoPin(char[] var1, char[] var2) {
        this.RY1.VerifySoPin(var1, var2);
    }

    public void RY1_ProduceSoPin(char[] var1, char var2, char[] var3) {
        this.RY1.ProduceSoPin(var1, var2, var3);
    }

    public void RY1_GenRandom(char[] var1) {
        this.RY1.GenRandom(var1);
    }

    public void RY1_SetTryCountForSoPin(char var1) {
        this.RY1.SetTryCountForSoPin(var1);
    }

    public void RY1_Read(short var1, short var2, char[] var3, short[] var4, char var5) {
        this.RY1.Read(var1, var2, var3, var4, var5);
    }

    public void RY1_Write(short var1, short var2, char[] var3, short[] var4, char var5) {
        this.RY1.Write(var1, var2, var3, var4, var5);
    }

    public void RY1_ResetSecurityState() {
        this.RY1.ResetSecurityState();
    }

    public void RY1_SetTDesKey(char var1, char[] var2) {
        this.RY1.SetTDesKey(var1, var2);
    }

    public void RY1_TDesEnc(char var1, char[] var2, int var3) {
        this.RY1.TDesEnc(var1, var2, var3);
    }

    public void RY1_TDesDec(char var1, char[] var2, int var3) {
        this.RY1.TDesDec(var1, var2, var3);
    }

    public void RY1_GenRSAKey(char var1, char var2, byte[] var3, byte[] var4) {
        this.RY1.GenRSAKey(var1, var2, var3, var4);
    }

    public void RY1_SetRSAKey(char var1, char var2, byte[] var3, byte[] var4) {
        this.RY1.SetRSAKey(var1, var2, var3, var4);
    }

    public void RY1_RSAEnc(char var1, char var2, char[] var3, int var4) {
        this.RY1.RSAEnc(var1, var2, var3, var4);
    }

    public void RY1_RSADec(char var1, char var2, char[] var3, int var4) {
        this.RY1.RSADec(var1, var2, var3, var4);
    }

    public void RY1_SetCounter(char var1) {
        this.RY1.SetCounter(var1);
    }

    public void RY1_GetCounter(char[] var1) {
        this.RY1.GetCounter(var1);
    }

    public void RY1_SetUpdatePacket(int var1, short var2, short var3, char[] var4, int var5) {
        this.RY1.SetUpdatePacket(var1, var2, var3, var4, var5);
    }

    public void RY1_GenUpdatePacket(byte[] var1, char[] var2, short[] var3) {
        this.RY1.GenUpdatePacket(var1, var2, var3);
    }

    public void RY1_Update(char[] var1, short var2) {
        this.RY1.Update(var1, var2);
    }

    public void RY1_GetErrInfo(int var1) {
        this.RY1.GetErrInfo(var1);
    }
}

package com.neworange.netty4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/30 14:23
 * @description
 */
public class TimeServer1 implements Runnable{
    private Socket socket;

    public TimeServer1(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader bufferedReader=null;
        PrintWriter out=null;
        try {
            bufferedReader=new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out=new PrintWriter(this.socket.getOutputStream(),true);
            String currentTime=null;
            String body=null;
            while (true){
                body=bufferedReader.readLine();
                if (body==null){
                    break;
                }
                System.out.println("The time server receive order:"+body);
                currentTime="QUERY TIME ORDER".equalsIgnoreCase(body)?new java.util.Date(System.currentTimeMillis()).toString():"BAD ORDER";
                out.println(currentTime);
            }
        }catch (Exception exception){

        }finally {
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                }catch (IOException ioException){
                    ioException.printStackTrace();
                }
            }
            if(out!=null){
                out.close();
                out=null;
            }
            if(this.socket!=null){
                try {
                    this.socket.close();
                }catch (IOException ioException){
                    ioException.printStackTrace();
                }
                this.socket=null;
            }
        }

    }
}

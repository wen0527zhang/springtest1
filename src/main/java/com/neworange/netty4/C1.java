package com.neworange.netty4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/30 14:46
 * @description
 */
public class C1 {
    public static void main(String[] args) {
        int port=8080;
        Socket socket=null;
        BufferedReader in =null;
        PrintWriter out=null;
        try {
            socket=new Socket("127.0.0.1",port);
            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream(),true);
            out.println("Query time order");
            System.out.println("Send order 2 server succeed");
            String resp=in.readLine();
            System.out.println("Now is:"+resp);
        }catch (Exception ex){

        }finally {
            if(in!=null){
                try {
                    in.close();
                }catch (IOException ioException){
                    ioException.printStackTrace();
                }
            }
            if(out!=null){
                out.close();
                out=null;
            }
            if(socket!=null){
                try {
                    socket.close();
                }catch (IOException ioException){
                    ioException.printStackTrace();
                }
                socket=null;
            }
        }
    }
}

package com.neworange.utils;

import org.bytedeco.opencv.opencv_core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/6/18 14:27
 * @description
 */
public class ImageUI extends JComponent {
    private BufferedImage image;

    public ImageUI() {
        this.image = null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (image == null) {
            g2d.setPaint(Color.BLACK);
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        } else {
            g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        }
    }

    /**
     * 显示图片
     * @param title
     * @param src
     */
    public void imshow(String title, Mat src) {
        this.image = convert2BufferedImage(src);
        //对话框
        JDialog ui = new JDialog();
        ui.setTitle(title);
        //设置布局
        ui.getContentPane().setLayout(new BorderLayout());
        //设置布局居中方式
        ui.getContentPane().add(this, BorderLayout.CENTER);
        //设置宽高
        ui.setSize(image.getWidth() + 16, image.getHeight() + 38);
        //设置可见
        ui.setVisible(true);
        //刷新
        this.repaint();
    }


    public void imshow(String title, BufferedImage src) {
        this.image = src;
        //对话框
        JDialog ui = new JDialog();
        ui.setTitle(title);
        //设置布局
        ui.getContentPane().setLayout(new BorderLayout());
        //设置布局居中方式
        ui.getContentPane().add(this, BorderLayout.CENTER);
        //设置宽高
        ui.setSize(image.getWidth() + 16, image.getHeight() + 38);
        //设置可见
        ui.setVisible(true);
        //刷新
        this.repaint();
    }

    private BufferedImage convert2BufferedImage(Mat matrix) {

        int cols=matrix.cols();
        int rows=matrix.rows();
        int elemSize=(int)matrix.elemSize();
        byte[] data=new byte[cols*rows*elemSize];

        matrix.data().get(data);

        int type = 0;
        switch(matrix.channels()){
            case 1:
                type=BufferedImage.TYPE_BYTE_GRAY;
                break;
            case 3:
                type= BufferedImage.TYPE_3BYTE_BGR;
                byte b;
                for(int i=0;i<data.length;i=i+3){
                    b=data[i];
                    data[i]=data[i+2];
                    data[i+2]=b;
                }
                break;
            default:
                return null;
        }
        BufferedImage image=new BufferedImage(cols,rows,type);
        image.getRaster().setDataElements(0, 0,cols,rows,data);
        return image;
    }
}

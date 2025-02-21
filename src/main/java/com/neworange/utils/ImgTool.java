package com.neworange.utils;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/6/18 14:29
 * @description
 */
public class ImgTool {
    public static Map<String, NDArray> normalize_kp(Map<String, NDArray> kp_source, Map<String, NDArray> kp_driving, Map<String, NDArray> kp_driving_initial, double adapt_movement_scale,
                                                    boolean use_relative_movement, boolean use_relative_jacobian) {

        Map<String, NDArray> kp_new = new HashMap<>();
        kp_driving.forEach((k, v) -> {
            kp_new.put(k, v.duplicate());
        });

        if (adapt_movement_scale != 0) {
            double source_area = getHullArea(kp_source);
            double driving_area = getHullArea(kp_driving_initial);
            adapt_movement_scale = Math.sqrt(source_area) / Math.sqrt(driving_area);
        } else {
            adapt_movement_scale = 1;
        }
        if (use_relative_movement) {
            NDArray kp_value_diff = kp_driving.get("value").sub(kp_driving_initial.get("value"));
            kp_value_diff = kp_value_diff.mul(adapt_movement_scale);
            kp_new.put("value", kp_value_diff.add(kp_source.get("value")));

            if (use_relative_jacobian) {
                //kp_driving_initial.get("jacobian").erfinv() //此处需要逆矩阵
            /*Mat m1 = new Mat(new Size(10,2),opencv_core.CV_32FC2);
            FloatRawIndexer imgIndex = m1.createIndexer();
            MatExpr mT = m1.inv();
            NDManager manger = NDManager.newBaseManager();
            kp_driving_diff = manger.create(mT.asByteBuffer(), kp_driving_diff.getShape());*/
                NDArray kp_driving_diff = kp_driving_initial.get("jacobian");
                NDManager manger = NDManager.newBaseManager();
                NDArray jacobian_diff = null;
                // kp_driving.get("jacobian").matMul(ni2Nd(nd2Ni(kp_driving_diff),manger));
                kp_new.put("jacobian", jacobian_diff.matMul(kp_source.get("jacobian")));

            }
        }
        return kp_new;
    }

  /*  private static INDArray nd2Ni(NDArray nd){
    	Shape ks = nd.getShape();
    	int rows = Long.valueOf(ks.get(0)).intValue();
    	int cols = Long.valueOf(ks.get(1)).intValue();
    	int zs = Long.valueOf(ks.get(2)).intValue();
    	float[][][] data = new float[rows][cols][zs];
    	for(int i=0;i<rows;i++){
    		for(int j=0;j<cols;j++){
    			for(int k=0;k<zs;k++){
    				data[i][j][k] = nd.get(i).get(j).getFloat(k);
            	}
        	}
    	}
    	INDArray ni = Nd4j.create(data);
    	return InvertMatrix.invert(ni, false);
    }

    private static NDArray ni2Nd(INDArray ni,NDManager manger ){
    	long[] ks = ni.shape();
    	int rows = Long.valueOf(ks[0]).intValue();
    	int cols = Long.valueOf(ks[1]).intValue();
    	int zs = Long.valueOf(ks[2]).intValue();
    	float[] data = new float[rows * cols * zs];
    	int index = 0;
    	for(int i=0;i<rows;i++){
    		for(int j=0;j<cols;j++){
    			for(int k=0;k<zs;k++){
    				data[index++] = ni.getFloat(i,j,k);
            	}
        	}
    	}
    	return manger.create(data,new Shape(10,2,2));
    }
    */

    private static double getHullArea(Map<String, NDArray> source) {
        Mat hull1 = new Mat();
        NDArray value = source.get("value");
        int vsize = Long.valueOf(value.getShape().get(0)).intValue();
        List<Point2f> points = new ArrayList<>();
        for (int i = 0; i < vsize; i++) {
            float x = value.get(i).getFloat(0);
            float y = value.get(i).getFloat(1);
            // System.out.println(x+"="+y);
            points.add(new Point2f(x, y));
        }
        Mat points2m = new Mat(points.size());
        for (Point2f pf : points) {
            Mat pm = new Mat(pf);
            points2m.push_back(pm);
        }
        opencv_imgproc.convexHull(points2m, hull1, false, true);
        return opencv_imgproc.contourArea(hull1);
    }
}

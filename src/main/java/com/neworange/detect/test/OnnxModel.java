package com.neworange.detect.test;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class OnnxModel {
    private final OrtEnvironment env;
    private final OrtSession session;
    private final String[] inputNames;
    private final String[] outputNames;

    public OnnxModel(String modelPath) throws OrtException {
        this.env = OrtEnvironment.getEnvironment();
        this.session = env.createSession(modelPath, new OrtSession.SessionOptions());
        this.inputNames = session.getInputNames().toArray(new String[0]);
        this.outputNames = session.getOutputNames().toArray(new String[0]);
    }

    public float[] forward(float[] inputs) throws OrtException {

        OnnxTensor tensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(inputs), new long[]{1, inputs.length});
        Map<String, OnnxTensor> inputsMap = new HashMap<>();
        inputsMap.put(session.getInputNames().iterator().next(), tensor);
        OrtSession.Result result = session.run(inputsMap, session.getOutputNames());
        float[][] output = new float[1][outputNames.length];
        return output[0];
    }
    public int[] forward(Mat inputs) throws OrtException {

//        OnnxTensor tensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(inputs), new long[]{1, inputs.length});
        OnnxTensor tensor = preprocessInput(inputs, inputs.height(), inputs.width());
        Map<String, OnnxTensor> inputsMap = new HashMap<>();
        inputsMap.put(session.getInputNames().iterator().next(), tensor);
        OrtSession.Result result = session.run(inputsMap, session.getOutputNames());
//        float[][] output = new float[1][outputNames.length];
        int[] output = new int[outputNames.length];
        for (int i = 0; i < outputNames.length; i++) {
            output[i]=Integer.valueOf(outputNames[i]);
        }
        return output;
    }
    public  OnnxTensor preprocessInput(Mat image, int desiredHeight, int desiredWidth) throws OrtException {
        Mat resizedImage = new Mat();
        Imgproc.resize(image, resizedImage, new Size(224, 224));
//
        Mat floatImage = new Mat();
        resizedImage.convertTo(floatImage, CvType.CV_32F);
        int rows = floatImage.rows() > 0 ? floatImage.rows() : (int) (floatImage.total() / floatImage.channels());
        int cols = floatImage.cols() > 0 ? floatImage.cols() : floatImage.channels();
        int channels = floatImage.channels();
        float[] imageArray = new float[(int) (rows * cols * channels)];
        floatImage.get(0, 0, imageArray);

//        long[] shape = {1, floatImage.channels(), rows, cols};
        long[] shape = {1, channels, rows, cols}; // 假设输入形状是 [batch_size, num_channels, height, width]

        return OnnxTensor.createTensor(env, FloatBuffer.wrap(imageArray), shape);
    }
}

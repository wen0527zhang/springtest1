package com.neworange.utils;

import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import ai.onnxruntime.OrtException;
import com.neworange.detect.ImageFeatureDetect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 图片特征获取
 */
public class ImageFeatureUtil {
    private static final Logger logger = LoggerFactory.getLogger(ImageFeatureUtil.class);

    private ImageFeatureUtil() {}

    //获取图片特征
    public static float[] runOcr(String path) throws IOException, ModelException, TranslateException, OrtException {
        Path imageFile = Paths.get(path);
        Image image = ImageFactory.getInstance().fromFile(imageFile);
        return runOcr(image);
    }
    public static float[] runOcr(Image image) throws IOException, ModelException, TranslateException, OrtException {
        ImageFeatureDetect imageFeatureDetect = new ImageFeatureDetect("image_feature.zip");
        Criteria<Image, float[]> criteria = imageFeatureDetect.criteria();
        try (ZooModel model = ModelZoo.loadModel(criteria);
             Predictor<Image, float[]> predictor = model.newPredictor()) {
            float[] predict = predictor.predict(image);
            return predict;
        }
    }
}

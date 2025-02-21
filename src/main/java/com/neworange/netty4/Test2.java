package com.neworange.netty4;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import com.neworange.utils.ImageUI;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/6/18 13:58
 * @description
 */
public class Test2 {
    public static void main(String[] args) throws IOException, ModelException, TranslateException {
        String url = "https://github.com/awslabs/djl/raw/master/examples/src/test/resources/dog_bike_car.jpg";
        //BufferedImage img = BufferedImageUtils.fromUrl(url);
        Image img = ImageFactory.getInstance().fromUrl(url);
        BufferedImage wrappedImage = (BufferedImage)img.getWrappedImage();
        new ImageUI().imshow("a",wrappedImage);
        Criteria<Image, DetectedObjects> criteria =
                Criteria.builder()
                        .optApplication(Application.CV.OBJECT_DETECTION)
                        .setTypes(Image.class, DetectedObjects.class)
                        .optFilter("backbone", "resnet50")
                        .optProgress(new ProgressBar())
                        .build();

        try (ZooModel<Image, DetectedObjects> model = ModelZoo.loadModel(criteria)) {
            try (Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
                DetectedObjects detection = predictor.predict(img);
                System.out.println(detection);
            }
        }

    }
}

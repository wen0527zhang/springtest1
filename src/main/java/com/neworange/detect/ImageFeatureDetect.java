package com.neworange.detect;

import ai.djl.modality.cv.Image;
import ai.djl.ndarray.NDList;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.onnxruntime.OrtException;
import com.neworange.detect.translator.AbstractDjlTranslator;
import com.neworange.detect.translator.BaseImageTranslator;

import java.util.Map;

/**
 * @author gc.x
 * @date 2023-11-10
 */
public  class ImageFeatureDetect extends AbstractDjlTranslator<float[]> {

    public ImageFeatureDetect(String modelName) throws OrtException {
        super(modelName,null);
    }
    @Override
    protected Translator<Image, float[]> getTranslator(Map<String, Object> arguments) {
        BaseImageTranslator.BaseBuilder<?> builder=new BaseImageTranslator.BaseBuilder<BaseImageTranslator.BaseBuilder>() {
            @Override
            protected BaseImageTranslator.BaseBuilder self() {
                return this;
            }
        };
        return new BaseImageTranslator<float[]>(builder) {
            @Override
            public float[] processOutput(TranslatorContext translatorContext, NDList ndList) throws Exception {
                return ndList.get(0).toFloatArray();

            }
        };
    }
    @Override
    protected Class<float[]> getClassOfT() {
        return float[].class;
    }

    @Override
    protected String getEngine() {
        return "PyTorch";
    }

}
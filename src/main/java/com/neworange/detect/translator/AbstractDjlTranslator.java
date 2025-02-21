package com.neworange.detect.translator;

import ai.djl.modality.cv.Image;
import ai.djl.repository.zoo.Criteria;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Translator;
import com.neworange.utils.JarFileUtils;
import com.neworange.utils.PathConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public abstract class AbstractDjlTranslator<T> {

    public String modelName;

    public Map<String, Object> arguments;

    static {
        // 加载opencv动态库，
        //System.load(ClassLoader.getSystemResource("lib/opencv_java470-无用.dll").getPath());
        nu.pattern.OpenCV.loadLocally();
    }

    public AbstractDjlTranslator(String modelName, Map<String, Object> arguments) {
        this.modelName = modelName;
        this.arguments=arguments;
    }

    public Criteria<Image, T> criteria() {
        Translator<Image, T> translator = getTranslator(arguments);
        try {
            JarFileUtils.copyFileFromJar("/onnx/models/" + modelName, PathConstants.ONNX, null, false, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String modelPath = PathConstants.TEMP_DIR + PathConstants.ONNX + File.separator + modelName;
        //定义了模型输入和输出
        Criteria<Image, T> criteria = Criteria.builder()
                        .setTypes(Image.class, getClassOfT())  // 定义模型输入和输出
                        //.optModelUrls(modelPath)
                        .optModelPath(Paths.get(modelPath))
                        //.optModelName("ultranet")//设置一下模型名称
                        .optTranslator(translator)//模型的输入和输出是一个Tensor类型
                        .optEngine(getEngine()) // Use PyTorch engine
                        .optProgress(new ProgressBar())
                        .build();
        return criteria;
    }

    protected abstract Translator<Image, T> getTranslator(Map<String, Object> arguments);

    // 获取 T 类型的函数
    protected abstract Class<T> getClassOfT();

    protected abstract String getEngine();
}


package com.neworange.detect.test;

import ai.onnxruntime.OrtException;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.io.FileUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlantIdentifier extends OnnxModel {
    private Map<Integer, Map<String, String>> labelNameDict;
    private Map<String, List<Integer>> familyDict;
    private Map<String, List<Integer>> genusDict;
    private Map<String, String> familyNameMap;
    private Map<String, String> genusNameMap;
    private List<String> names;
    private List<String> familyNames;
    private List<String> genusNames;

    static {
        // 加载opencv动态库，
        //System.load(ClassLoader.getSystemResource("lib/opencv_java470-无用.dll").getPath());
        nu.pattern.OpenCV.loadLocally();
    }

    public PlantIdentifier() throws OrtException {
        //String plantid_model = ClassLoader.getSystemResource("/onnx/models/vosk-model-small-cn-0.22.zip").getPath();
        //String model_path = "src\\main\\resources\\model\\helmet_1_25200_n.onnx";
        super("onnx/models/quarrying_plantid_model.onnx");

        // 其他代码...
        this.labelNameDict = getLabelNameDict("onnx/models/quarrying_plantid_label_map.txt");
        this.familyDict = getFamilyAndGenusDict("onnx/models/quarrying_plantid_label_map.txt").get("familyDict");
        this.genusDict = getFamilyAndGenusDict("onnx/models/quarrying_plantid_label_map.txt").get("genusDict");
        this.familyNameMap = loadJson("onnx/models/family_name_map.json");
        this.genusNameMap = loadJson("onnx/models/genus_name_map.json");

        this.names = new ArrayList<>();
        for (int i = 0; i < labelNameDict.size(); i++) {
            String chineseName = labelNameDict.get(i).get("chinese_name");
            names.add(chineseName);
        }

        this.familyNames = new ArrayList<>(familyDict.keySet());
        this.genusNames = new ArrayList<>(genusDict.keySet());
    }

    private static Map<Integer, Map<String, String>> getLabelNameDict(String filename) {
        List<String> records = loadList(filename);
        Map<Integer, Map<String, String>> labelNameDict = new HashMap<>();
        for (String record : records) {
            String[] parts = record.split(",");
            int label = Integer.parseInt(parts[0]);
            String chineseName = parts[1];
            Map<String, String> nameDict = new LinkedHashMap<>();
            if(parts.length>2){
                String latinName = parts[2];
                nameDict.put("latin_name", latinName);
            }
            nameDict.put("chinese_name", chineseName);
            labelNameDict.put(label, nameDict);
        }
        return labelNameDict;
    }
    private static List<String> loadList(String filename){
        try {
            List<String> strings = FileUtils.readLines(new File(filename), "UTF-8");
            return strings;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private static Map<String, String> loadJson(String filename){
        Map<String,String> map=new HashMap<>();
        String json = null;
        try {
            json = FileUtils.readFileToString(new File(filename), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        json= json.replace("{","");
        json= json.replace("}","");
        String[] split = json.split(",");
        for (String kv : split) {
            String[] split1 = kv.split(":");
            String k = split1[0].trim().replace("\"", "");
            String v = split1[1].trim().replace("\"", "");
            map.put(k,v);
        }
        return map;
    }
    public static float[] matToFloatArray(Mat mat) {
        int rows = mat.rows() > 0 ? mat.rows() : (int) (mat.total() / mat.channels());
        int cols = mat.cols() > 0 ? mat.cols() : mat.channels();

        if (mat.depth() != CvType.CV_32F || mat.channels() != 1) {
            throw new IllegalArgumentException("Unsupported Mat type. Expected CV_32FC1.");
        }

        float[] array = new float[rows * cols];
        mat.get(0, 0, array);

        return array;
    }
    private static Map<String, Map<String, List<Integer>>> getFamilyAndGenusDict(String filename) {
        List<String> records = loadList(filename);
        Map<String, List<Integer>> familyDict = new LinkedHashMap<>();
        Map<String, List<Integer>> genusDict = new LinkedHashMap<>();

        for (String record : records) {
            String[] parts = record.split(",");
            int label = Integer.parseInt(parts[0]);
            String chineseName = parts[1];
            String[] underscoreParts = chineseName.split("_");
            if (underscoreParts.length == 1) {
                String familyName = underscoreParts[0];
                familyDict.computeIfAbsent(familyName, k -> new ArrayList<>()).add(label);
                genusDict.computeIfAbsent(familyName, k -> new ArrayList<>()).add(label);
            } else if (underscoreParts.length > 1) {
                String familyName = underscoreParts[0];
                String genusName = underscoreParts[0] + "_" + underscoreParts[1];
                familyDict.computeIfAbsent(familyName, k -> new ArrayList<>()).add(label);
                genusDict.computeIfAbsent(genusName, k -> new ArrayList<>()).add(label);
            }
        }

        Map<String, Map<String, List<Integer>>> result = new HashMap<>();
        result.put("familyDict", familyDict);
        result.put("genusDict", genusDict);
        return result;
    }

//    private static float[] preprocess(Mat image) {
//        checkImageDtypeAndShape(image);
//
//        // 图像尺寸归一化
//        Mat resized = resizeImageShort(image, 224,false,"bilinear");
//        Mat cropped = centerCrop(resized, 224, 224,true);
//        // 图像通道归一化
//        Mat normalized = normalizeImageChannel(cropped, true);
//        // 图像 dtype 和数值范围归一化
//        List<Float> mean = Arrays.asList(0.485f, 0.456f, 0.406f);
//        List<Float> stddev = Arrays.asList(0.229f, 0.224f, 0.225f);
//        Mat normalizedValue = normalizeImageValue(normalized, mean, stddev, "auto");
//        // 转为张量
//        Mat transposed = transposeImage(normalizedValue);
//        Mat expanded = expandDims(transposed);
//        return matToFloatArray(expanded);
//    }
    private static Mat preprocess(Mat image) {
        checkImageDtypeAndShape(image);

        // 图像尺寸归一化
        Mat resized = resizeImageShort(image, 224,false,"bilinear");
        Mat cropped = centerCrop(resized, 224, 224,true);
        // 图像通道归一化
        Mat normalized = normalizeImageChannel(cropped, true);
        // 图像 dtype 和数值范围归一化
        List<Float> mean = Arrays.asList(0.485f, 0.456f, 0.406f);
        List<Float> stddev = Arrays.asList(0.229f, 0.224f, 0.225f);
        Mat normalizedValue = normalizeImageValue(normalized, mean, stddev, "auto");
        // 转为张量
        Mat transposed = transposeImage(normalizedValue);
        Mat expanded = expandDims(transposed);
        return resized;
    }
    public static Mat transposeImage(Mat image) {
        assert image.channels() == 3; // Assuming input image has 3 channels (RGB/BGR)

        Mat transposed = new Mat(2,0,1);
        Core.transpose(image, transposed);

        return transposed;
    }

    public static Mat expandDims(Mat image) {
        assert image.channels() == 3; // Assuming input image has 3 channels (RGB/BGR)

        int[] shape = {1, image.channels(), image.rows(), image.cols()};
        Mat reshaped = new Mat();
        image.reshape(1, new MatOfInt(shape).toArray()).copyTo(reshaped);

        return reshaped;
    }
    public static Mat normalizeImageValue(Mat image, List<Float> mean, List<Float> std, String rescaleFactor) {
        // Convert mean and std to float arrays
        double[] meanArray = mean.stream().mapToDouble(Double::valueOf).toArray();
        double[] stdArray = std.stream().mapToDouble(Double::valueOf).toArray();
        // Normalize image values
        Mat normalized = new Mat();
        image.convertTo(normalized, CvType.CV_32F);
        Core.subtract(normalized, new Scalar(meanArray), normalized);
        Core.divide(normalized, new Scalar(stdArray), normalized);

        // Rescale the image if necessary
        if (rescaleFactor.equals("auto")) {
            int maxValue = 255;
            if (normalized.depth() == CvType.CV_16U) {
                maxValue = 65535;
            }
            Core.multiply(normalized, new Scalar(maxValue), normalized);
        } else if (rescaleFactor != null && !rescaleFactor.isEmpty()) {
            double scaleFactor = Double.parseDouble(rescaleFactor);
            Core.multiply(normalized, new Scalar(scaleFactor), normalized);
        }

        return normalized;
    }
    public static Mat normalizeImageChannel(Mat image, boolean swapRB) {
        assert image.channels() <= 4; // Assuming input image has maximum 4 channels

        if (image.channels() == 1) {
            Imgproc.cvtColor(image, image, Imgproc.COLOR_GRAY2BGR);
        } else if (image.channels() == 3) {
            if (swapRB) {
                Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGB);
            }
        } else if (image.channels() == 4) {
            if (swapRB) {
                Imgproc.cvtColor(image, image, Imgproc.COLOR_BGRA2RGB);
            } else {
                Imgproc.cvtColor(image, image, Imgproc.COLOR_BGRA2BGR);
            }
        }

        return image;
    }
    public static Mat centerCrop(Mat image, int dstWidth, int dstHeight, boolean strict) {
        assert image.channels() == 3; // Assuming input image has 3 channels (RGB/BGR)

        int srcHeight = image.rows();
        int srcWidth = image.cols();

        if (strict) {
            assert (srcHeight >= dstHeight) && (srcWidth >= dstWidth);
        }

        int cropTop = Math.max((srcHeight - dstHeight) / 2, 0);
        int cropLeft = Math.max((srcWidth - dstWidth) / 2, 0);

        Rect roi = new Rect(cropLeft, cropTop, dstWidth, dstHeight);
        Mat cropped = new Mat(image, roi);

        return cropped;
    }
    public static Mat resizeImageShort(Mat image, int dstSize, boolean returnScale, String interpolation) {
        assert image.channels() == 3; // Assuming input image has 3 channels (RGB/BGR)

        int srcHeight = image.rows();
        int srcWidth = image.cols();

        double scale = Math.max((double) dstSize / srcWidth, (double) dstSize / srcHeight);
        int dstWidth = (int) Math.round(scale * srcWidth);
        int dstHeight = (int) Math.round(scale * srcHeight);

        Size newSize = new Size(dstWidth, dstHeight);
        Mat resizedImage = new Mat();
        Imgproc.resize(image, resizedImage, newSize, 0, 0, getInterpolationCode(interpolation));

        if (!returnScale) {
            return resizedImage;
        } else {
            return resizedImage;
        }
    }
    private static int getInterpolationCode(String interpolation) {
        switch (interpolation) {
            case "nearest":
                return Imgproc.INTER_NEAREST;
            case "bilinear":
                return Imgproc.INTER_LINEAR;
            case "bicubic":
                return Imgproc.INTER_CUBIC;
            default:
                throw new IllegalArgumentException("Unknown interpolation mode: " + interpolation);
        }
    }
    public static void checkImageDtypeAndShape(Mat image) {
        if (image.empty()) {
            throw new IllegalArgumentException("Empty image!");
        }

        int numChannels = image.channels();
        if (numChannels != 3) {
            throw new IllegalArgumentException("Unsupported number of channels: " + numChannels + ". Expected 3.");
        }
    }
    public Map<String, Object> predict(Mat image) {
        try {
//            float[] inputs = preprocess(image);
             Mat inputs = preprocess(image);

//            float[] logits = forward(inputs);
//            float[] probs = softmax(logits);
             int[] probs = forward(inputs);


            Map<String, Object> results = new HashMap<>();
            results.put("probs", probs);
            results.put("status", 0);

//            results.put("family_probs", getCollectiveProbs(probs, familyDict));
//            results.put("genus_probs", getCollectiveProbs(probs, genusDict));

            return results;
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", -2);
            errorResult.put("message", "Inference error.");
            errorResult.put("results", new HashMap<>());
            return errorResult;
        }
    }
    public static float[] getCollectiveProbs(int[] probs, Map<String, List<Integer>> collectiveDict) {
        int batch_size = 1;
        int num_collective = collectiveDict.size();
        float[] collective_probs = new float[num_collective];

        for (int collective_ind = 0; collective_ind < num_collective; collective_ind++) {
            List<Integer> taxon_indices = collectiveDict.get(collective_ind);
            float collective_prob = 0.0f;

            for (int batch_ind = 0; batch_ind < batch_size; batch_ind++) {
                for (int index : taxon_indices) {
                    collective_prob += probs[batch_ind * probs.length + index];
                }
            }

            collective_probs[collective_ind] = collective_prob;
        }

        return collective_probs;
    }
    public static float[] softmax(float[] x) {
        float max = Float.NEGATIVE_INFINITY;
        for (float value : x) {
            if (value > max) {
                max = value;
            }
        }

        float sumExp = 0.0f;
        for (int i = 0; i < x.length; i++) {
            x[i] = (float) Math.exp(x[i] - max);
            sumExp += x[i];
        }

        for (int i = 0; i < x.length; i++) {
            x[i] /= sumExp;
        }

        return x;
    }
    public static List<Integer> topK(float[] probs, int k) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < probs.length; i++) {
            indices.add(i);
        }

        indices.sort(Comparator.comparingDouble(i -> -probs[i]));
        return indices.subList(0, Math.min(k, indices.size()));
    }
    public Map<String, Object> identify(Mat image, int topk) {
        assert topk > 0;
        if (topk <= 0) {
            int temp = Math.max(labelNameDict.size(), familyDict.size());
            topk = Math.max(temp, genusDict.size());
        }

        List<Map<String, Object>> results = new ArrayList<>();
        List<Map<String, Object>> familyResults = new ArrayList<>();
        List<Map<String, Object>> genusResults = new ArrayList<>();

        Map<String, Object> outputs = predict(image);
        int status = (int) outputs.get("status");
        String message = (String) outputs.get("message");
        if (status != 0) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", status);
            errorResult.put("message", message);
            errorResult.put("results", results);
            errorResult.put("family_results", familyResults);
            errorResult.put("genus_results", genusResults);
            return errorResult;
        }
        int[] probs= (int[]) outputs.get("probs");
//        float[] probs = (float[]) outputs.get("probs");
//        float[] familyProbs = (float[]) outputs.get("family_probs");
//        float[] genusProbs = (float[]) outputs.get("genus_probs");

//        int taxonTopk = Math.min(probs.length, topk);
//        List<Integer> topkIndices = topK(probs, taxonTopk);
        for (int index = 0; index < probs.length; index++) {
            Map<String, Object> oneResult = new LinkedHashMap<>();
            oneResult.put("chinese_name", labelNameDict.get(index).get("chinese_name"));
            results.add(oneResult);
        }
//        for (int index : topkIndices) {
//            Map<String, Object> oneResult = new LinkedHashMap<>();
//            oneResult.put("chinese_name", labelNameDict.get(index).get("chinese_name"));
//            oneResult.put("latin_name", labelNameDict.get(index).get("latin_name"));
//            oneResult.put("probability", probs[index]);
//            results.add(oneResult);
//        }
//
//        int familyTopk = Math.min(familyProbs.length, topk);
//        List<Integer> familyTopkIndices = topK(familyProbs, familyTopk);
//        for (int index : familyTopkIndices) {
//            Map<String, Object> oneResult = new LinkedHashMap<>();
//            String familyName = familyNames.get(index);
//            oneResult.put("chinese_name", familyName);
//            oneResult.put("latin_name", familyNameMap.getOrDefault(familyName, ""));
//            oneResult.put("probability", familyProbs[index]);
//            familyResults.add(oneResult);
//        }
//
//        int genusTopk = Math.min(genusProbs.length, topk);
//        List<Integer> genusTopkIndices = topK(genusProbs, genusTopk);
//        for (int index : genusTopkIndices) {
//            Map<String, Object> oneResult = new LinkedHashMap<>();
//            String genusName = genusNames.get(index);
//            oneResult.put("chinese_name", genusName);
//            oneResult.put("latin_name", genusNameMap.getOrDefault(genusName, ""));
//            oneResult.put("probability", genusProbs[index]);
//            genusResults.add(oneResult);
//        }

        Map<String, Object> successResult = new HashMap<>();
        successResult.put("status", status);
        successResult.put("message", message);
        successResult.put("results", results);
        successResult.put("family_results", familyResults);
        successResult.put("genus_results", genusResults);
        return successResult;
    }

    public static void main(String[] args) throws OrtException {
        List<String> srcDirs = Arrays.asList("/images");
        List<File> srcFiles = new ArrayList<>();
        for (String srcDir : srcDirs) {
            File[] files = new File(srcDir).listFiles();
            if (files != null) {
                srcFiles.addAll(Arrays.asList(files));
            }
        }
        srcFiles.sort((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

        PlantIdentifier plantIdentifier = new PlantIdentifier();
        for (int i = 0; i < srcFiles.size(); i++) {
            File file = srcFiles.get(i);
            Mat image = Imgcodecs.imread(file.getAbsolutePath());
            if (image.empty()) {
                continue;
            }
            Map<String, Object> identify = plantIdentifier.identify(image, 2);
            if (identify != null) {
                System.out.println(JSONObject.toJSONString(identify));
            }
        }
    }
}

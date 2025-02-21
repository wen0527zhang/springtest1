//package com.neworange.netty4;
//
//import ai.djl.inference.Predictor;
//
//import ai.djl.modality.cv.ImageFactory;
//import ai.djl.modality.cv.transform.Normalize;
//import ai.djl.modality.cv.transform.Resize;
//import ai.djl.modality.cv.transform.ToTensor;
//import ai.djl.ndarray.NDArray;
//import ai.djl.ndarray.NDList;
//import ai.djl.ndarray.index.NDIndex;
//import ai.djl.ndarray.types.DataType;
//import ai.djl.repository.zoo.Criteria;
//import ai.djl.repository.zoo.ModelNotFoundException;
//import ai.djl.repository.zoo.ZooModel;
//import ai.djl.translate.Translator;
//import ai.djl.translate.TranslatorContext;
//
//import java.awt.*;
//import java.io.IOException;
//import java.nio.file.Paths;
//
///**
// * @author winter
// * @version 1.0.0
// * @date 2024/6/7 19:12
// * @description
// */
//public class Dclass1 {
//    public static void main(String[] args) {
//        Criteria<String, String> criteria = Criteria.builder()
//                .setTypes(String.class, String.class) //定义了模型输入和输出
//                .optTranslator(translator)//模型的输入和输出是一个Tensor类型
//                .optModelPath(Paths.get("/var/models/my_resnet50")) // search models in specified path
//                .optModelName("model/resnet50") // specify model file prefix
//                .build();
//        try {
//            //实例化
//            ZooModel model = criteria.loadModel();
//            Predictor predictor = model.newPredictor();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (ModelNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * 将自定义输入输出类转换为Tensor类型
//     */
//    private static Translator<String, String> translator = new Translator<String, String>() {
//        /**
//         * 将输入类对象转化为Tensor。这里的Input就是输入类对象，而NDList就是Tensor的集合
//         * @param ctx the toolkit for creating the input NDArray
//         * @param input the input object
//         * @return
//         * @throws Exception
//         */
//        @Override
//        public NDList processInput(TranslatorContext ctx, String input) throws Exception {
//            // 根据路径读取图片
//            Image image = ImageFactory.getInstance().fromFile(Paths.get(input));
//            NDArray ndArray = image.toNDArray(ctx.getNDManager());
//            // 在图片送入resnet前要做一些预处理，官方的例子中使用transforms，但为了本文的前后呼应，我这里就用上面将的NDArray的操作来完成
//            Resize resize = new Resize(256, 256);
//            ndArray = resize.transform(ndArray); // 将图片的大小resize到256x256
//
//            // py: transforms.CenterCrop(224)
//            // NDArray没有CenterCrop方法，但是我们可以通过切片的方式实现
//            ndArray = ndArray.get(new NDIndex("16:240, 16:240, :"));
//
//            // ToTensor会将Shape的(224,224,3)转变为(3,224,224)，并且将值从0-255缩放到0-1
//            ndArray = new ToTensor().transform(ndArray);
//
//            // py: transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
//            Normalize normalize = new Normalize(new float[]{0.485f, 0.456f, 0.406f}, new float[]{0.229f, 0.224f, 0.225f});
//            ndArray = normalize.transform(ndArray);
//
//            return new NDList(ndArray);  // resnet只接受一个Tensor
//
//        }
//
//        /**
//         * 模型输出的Tensor转换为自定义类
//         * @param ctx the toolkit used for post-processing
//         * @param ndList the output NDList after inference, usually immutable in engines like
//         *     PyTorch. @see <a href="https://github.com/deepjavalibrary/djl/issues/1774">Issue 1774</a>
//         * @return
//         * @throws Exception
//         */
//        @Override
//        public String processOutput(TranslatorContext ctx, NDList ndList) throws Exception {
//            // resnet只返回一个tensor，所以要get(0)
//            int index = list.get(0).argMax().toType(DataType.INT32, false).getInt();
//            // 由于resnet可以识别1000种物体，这里我就只给index了。
//            return index + "";
//
//        }
//    };
//
//}

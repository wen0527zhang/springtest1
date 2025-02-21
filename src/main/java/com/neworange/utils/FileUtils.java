package com.neworange.utils;


import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlObject;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.namespace.QName;
import java.io.*;
import java.net.URLEncoder;
import java.util.regex.Pattern;



/**
 * @author winter
 * @version 1.0.0
 * @date 2024/6/23 18:36
 * @ description
 */
public class FileUtils {
    /**
     * 文件上传
     *
     * @param multipartFiles 上传的文件数组
     * @param filePath       文件上传路径
     * @return
     */
    public static String uploadFile(MultipartFile[] multipartFiles, String filePath) {
        try {
            File sourceFile = new File(filePath);
            if (!sourceFile.exists()) {
                sourceFile.mkdirs();
            }
            if (multipartFiles.length > 0) {
                for (MultipartFile multipartFile : multipartFiles) {
                    if (multipartFile.isEmpty()) {
                        return "上传的文件为空！";
                    }
                    String fileName = multipartFile.getOriginalFilename();  //获取文件名
                    String path = filePath + File.separator + fileName;
                    String absolutePath = new File(path).getAbsolutePath();
                    File file = new File(absolutePath);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    multipartFile.transferTo(file);
                }
                return "上传成功！";
            }
            return "上传失败！";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "成功！";
    }

    /**
     * 文件下载
     *
     * @param fileUrl  下载的源文件路径
     * @param fileName 下载的源文件名称
     * @param res
     * @throws IOException
     */
    public static void download(String fileUrl, String fileName, HttpServletResponse res) throws IOException {
        File file = new File(fileUrl + File.separator + fileName);
        boolean flag = FileUtils.fileIsExist(fileUrl, fileName);
        if (flag) {
            InputStream inputStream = null;
            OutputStream os = null;
            try {
                inputStream = new FileInputStream(file);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                inputStream.close();
                byte[] data = outStream.toByteArray();
                res.reset();
                res.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                res.setContentType("application/octet-stream");
                os = res.getOutputStream();
                os.write(data);
                os.flush();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (os != null) {
                    os.close();
                }
            }
        } else {
            throw new IOException("下载失败，源文件不存在！");
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @param fileName 文件名称
     */
    public static boolean fileIsExist(String filePath, String fileName) {
        File fileSource = new File(filePath + File.separator + fileName); // 源文件对象
        if (fileSource.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 文件复制
     *
     * @param sourcePath 源文件夹目录
     * @param newPath    目标文件夹目录
     */
    public static void copyFile(String sourcePath, String newPath) {
        try {
            BufferedInputStream bufferedInputStream = null;
            BufferedOutputStream bufferedOutputStream = null;
            InputStream input = null;
            OutputStream output = null;
            File fileSource = new File(sourcePath); //源文件对象
            File fileDest = new File(newPath); //目的文件对象
            if (fileSource.exists() && fileSource.list() != null) {
                if (!fileDest.exists()) {
                    fileDest.mkdirs(); //目的文件不存在则先创建目的文件
                }
                String[] list = fileSource.list();
                for (String fileName : list) {
                    input = new FileInputStream(fileSource + File.separator + fileName);
                    bufferedInputStream = new BufferedInputStream(input);
                    output = new FileOutputStream(fileDest + File.separator + fileName);
                    bufferedOutputStream = new BufferedOutputStream(output);
                    byte[] bytes = new byte[2048];
                    int length;
                    while ((length = bufferedInputStream.read(bytes)) > 0) {
                        bufferedOutputStream.write(bytes, 0, length);
                    }
                    bufferedInputStream.close();
                    bufferedOutputStream.close();
                    input.close();
                    output.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件删除（删除文件夹下的所有文件）
     *
     * @param sourcePath 目标文件
     */
    public static void deleteFileList(String sourcePath) {
        if (sourcePath != null && !"".equals(sourcePath)) {
            File file = new File(sourcePath);
            String[] list = file.list();
            if (list != null && list.length > 0) {
                for (String name : list) {
                    File f = new File(sourcePath, name);
                    f.delete();
                }
            }
        }
    }

    /**
     * docx文档添加水印
     *
     * @param filePath       文件路径
     * @param sourceFileName 源文件名称
     * @param test           水印内容
     * @param outFileUrl     输出文件路径
     * @param outFileName    输出文件名称
     */
    public static void addWaterMark(String filePath, String sourceFileName, String test, String outFileUrl, String outFileName) {
        try {
            InputStream in = new FileInputStream(new File(filePath + File.separator + sourceFileName));
            OutputStream out = null;
            XWPFDocument document = new XWPFDocument(in);
            XWPFHeaderFooterPolicy xFooter = new XWPFHeaderFooterPolicy(document);
            xFooter.createWatermark(test);
            XWPFHeader header = xFooter.getHeader(3);
            XWPFParagraph paragraph = header.getParagraphArray(0);
            XmlObject[] xmlObjects = paragraph.getCTP().getRArray(0).getPictArray(0).selectChildren(
                    new QName("urn:schemas-microsoft-com:vml", "shape"));
            if (xmlObjects.length > 0) {
                com.microsoft.schemas.vml.CTShape ctshape = (com.microsoft.schemas.vml.CTShape) xmlObjects[0];
                ctshape.setFillcolor("#F08080");
                ctshape.setStyle(getWaterMarkStyle(ctshape.getStyle(), 40) + ";rotation:315");
            }
            out = new FileOutputStream(new File(outFileUrl + File.separator + outFileName));
            document.write(out);
            out.flush();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置水印字体格式、大小
     *
     * @param styleStr
     * @param height
     * @return
     */
    public static String getWaterMarkStyle(String styleStr, double height) {
        Pattern p = Pattern.compile(";");
        String[] strs = p.split(styleStr);
        for (String str : strs) {
            if (str.startsWith("height:")) {
                String heightStr = "height:" + height + "pt";
                styleStr = styleStr.replace(str, heightStr);
                break;
            }
        }
        return styleStr;
    }

    /**
     * PDF添加水印
     *
     * @param sourceUrl     PDF文件路径（包含文件名称）
     * @param outPutUrl     添加水印后的PDF输出路径
     * @param waterMarkName 水印内容
     */
    public static void addWaterMarkPDF(String sourceUrl, String outPutUrl, String waterMarkName) {
        try {
            File file = new File(sourceUrl);
            String fileName = file.getName();
            byte[] bytes = org.apache.commons.io.FileUtils.readFileToByteArray(file);
            if (fileName.toLowerCase().endsWith("pdf")) {
                FileUtils.addWaterMarkPDF(bytes, outPutUrl, waterMarkName); //PDF加水印
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * PDF加水印
     *
     * @param fileData
     * @param outPutUrl     添加水印后的PDF输出路径
     * @param waterMarkName 水印内容
     */
    private static void addWaterMarkPDF(byte[] fileData, String outPutUrl, String waterMarkName) {
        try {
            PdfReader reader = null;
            PdfStamper stamper = null;
            try {
                reader = new PdfReader(fileData);
                stamper = new PdfStamper(reader, new FileOutputStream(outPutUrl));
                BaseFont base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
                PdfGState gs = new PdfGState();
                gs.setFillOpacity(0.3f);
                gs.setStrokeOpacity(0.4f);
                PdfContentByte under = null;
                Rectangle pageRect = null;
                for (int i = 1, n = reader.getNumberOfPages(); i <= n; i++) {
                    pageRect = reader.getPageSizeWithRotation(i);
                    // 计算水印X,Y坐标
                    float x = pageRect.getWidth() / 2;
                    float y = pageRect.getHeight() / 2;
                    under = stamper.getOverContent(i);
                    under.saveState();
                    under.setGState(gs);
                    under.beginText();
                    under.setFontAndSize(base, 40); //40：字体大小
                    under.showTextAligned(Element.ALIGN_CENTER, waterMarkName, x, y, 30);
                    under.endText();
                }
            } finally { // 关闭流
                if (stamper != null) {
                    stamper.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

}

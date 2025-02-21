package com.neworange.isapi.utils;

import com.neworange.isapi.demo.entity.DeviceInfoDTO;
import com.neworange.isapi.demo.entity.form.ContentDisposition;
import org.apache.commons.codec.binary.Hex;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

/**
 * @author zhengxiaohui
 * @date 2024/1/11 18:44
 * @desc 支持大文件的摘要认证调用工具类
 * <p>
 * 调用的参考示例可以参见 com.isapi.isapi_java_demo.utils.CustomDigestCallUtilTest#digestCallSample() 示例说明
 */
public class CustomDigestCallUtil {

    /**
     * 摘要认证调用
     *
     * @param url              调用的isapi地址 /ISAPI/**
     * @param method           调用的方法 POST/GET/DELETE/PUT
     * @param formDataItemList 调用表单分割单元内容
     * @param deviceInfoDTO    调用的目标设备信息
     * @return
     * @throws Exception
     */
    public static String digestCall(String url, String method, List<ContentDisposition> formDataItemList, DeviceInfoDTO deviceInfoDTO) throws Exception {
        if (formDataItemList == null || formDataItemList.size() == 0) {
            throw new RuntimeException("formDataItemList is null!");
        }
        // szBoundary 表单单元分割符(参数随机自定义)
        String szBoundary = "MIME_boundary";
        List<ContentDisposition> firstCallForm = formDataItemList.subList(0, 1);
        byte[] byBodyParam = CustomDigestCallUtil.buildForm(firstCallForm, szBoundary);

        // [1] 第一次调用拿到摘要认证信息
        String authorization = CustomDigestCallUtil.httpSocket(
                method,
                "http://" + deviceInfoDTO.getDevIp() + ":" + deviceInfoDTO.getDevPort(),
                url,
                szBoundary,
                byBodyParam,
                false,
                "");
        System.out.println(authorization);
        System.out.println("\n");

        // [2] 计算摘要认证的信息
        String authInfo = CustomDigestCallUtil.calculateDigestAuthInfo(authorization, deviceInfoDTO.getUsername(), deviceInfoDTO.getPassword(), url, method);

        // [3] 第二次正式发起调用
        byte[] byBodyParam2 = CustomDigestCallUtil.buildForm(formDataItemList, szBoundary);
        String authorization2 = CustomDigestCallUtil.httpSocket(
                method,
                "http://" + deviceInfoDTO.getDevIp() + ":" + deviceInfoDTO.getDevPort(),
                url,
                szBoundary,
                byBodyParam2,
                true,
                authInfo);
        System.out.println(authorization2);
        return authorization2;
    }

    /**
     * 构建form-data http表单结构
     * 支持根据formDataItemList进行拼接
     *
     * @param formDataItemList
     * @param szBoundary
     * @return
     * @throws IOException
     */
    public static byte[] buildForm(List<ContentDisposition> formDataItemList, String szBoundary) throws IOException {
        byte[] res = new byte[0];
        for (int i = 0; i < formDataItemList.size(); i++) {
            ContentDisposition item = formDataItemList.get(i);
            if (item.getFilename() == null) {
                // 普通的字段类型
                StringBuilder httpContentBody = new StringBuilder();
                httpContentBody.append("--" + szBoundary + "\r\n");
                httpContentBody.append("Content-Disposition: form-data; name=\"" + item.getName() + "\"\r\n");
                httpContentBody.append("Content-Type: " + item.getContentType() + "\r\n");
                httpContentBody.append("\r\n");
                httpContentBody.append(item.getNameValue());
                httpContentBody.append("\r\n");

                int byteResNewLen = res.length + httpContentBody.toString().getBytes().length;
                byte[] resTemp = new byte[byteResNewLen];
                System.arraycopy(res, 0, resTemp, 0, res.length);
                System.arraycopy(httpContentBody.toString().getBytes(), 0, resTemp, res.length, httpContentBody.toString().getBytes().length);
                res = resTemp;
            } else {
                // 文件类型的数据
                StringBuilder httpContentBody = new StringBuilder();
                httpContentBody.append("--" + szBoundary + "\r\n");
                //filename参数中文件名称和实际导入文件名称一致
                httpContentBody.append("Content-Disposition: form-data; name=\"" + item.getName() + "\"; filename=\"" + item.getFilename() + "\"\r\n");
                httpContentBody.append("Content-Type: " + item.getContentType() + "\r\n");
                httpContentBody.append("\r\n");

                int byteResNewLen = res.length + httpContentBody.toString().getBytes().length;
                byte[] resTemp = new byte[byteResNewLen];
                System.arraycopy(res, 0, resTemp, 0, res.length);
                System.arraycopy(httpContentBody.toString().getBytes(), 0, resTemp, res.length, httpContentBody.toString().getBytes().length);
                res = resTemp;

                // 读取本地文件并插入文件的数据
                //读取本地文件中的二进制数据
                File file = new File(item.getFileLocalPath());
                FileInputStream uploadPic = new FileInputStream(file);
                int iFileLen = uploadPic.available();
                byte[] byteFile = new byte[iFileLen];
                uploadPic.read(byteFile);

                // 文件数据添加到结果数组中
                byteResNewLen = res.length + iFileLen + "\r\n".getBytes().length;
                resTemp = new byte[byteResNewLen];
                System.arraycopy(res, 0, resTemp, 0, res.length);
                System.arraycopy(byteFile, 0, resTemp, res.length, byteFile.length);

                System.arraycopy("\r\n".getBytes(), 0, resTemp, res.length + byteFile.length, "\r\n".getBytes().length);
                res = resTemp;
            }

            // 最后一个拼接结束符
            if (i + 1 >= formDataItemList.size()) {
                String endBoundary = "--" + szBoundary + "--\r\n";
                int byteResNewLen = res.length + endBoundary.getBytes().length;
                byte[] resTemp = new byte[byteResNewLen];
                System.arraycopy(res, 0, resTemp, 0, res.length);
                System.arraycopy(endBoundary.getBytes(), 0, resTemp, res.length, endBoundary.getBytes().length);
                res = resTemp;
            }
        }
        return res;
    }

    /**
     * 计算摘要认证调用的auth信息
     *
     * @param authorization 2次交互中的首次交互返回值
     * @param username      摘要认证用户名
     * @param password      摘要认证密码
     * @param url           调用的url地址
     * @param method        调用的方法
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String calculateDigestAuthInfo(String authorization, String username, String password, String url, String method) throws NoSuchAlgorithmException {
        //计算认证信息
        String qop = "";
        String realm = "";
        String nonce = "";
        String[] strAuthInfo = authorization.split(",");
        for (int i = 0; i < strAuthInfo.length; i++) {
            System.out.println(strAuthInfo[i]);
            if (strAuthInfo[i].contains("qop")) {
                qop = strAuthInfo[i].substring(strAuthInfo[i].indexOf("\"") + 1, strAuthInfo[i].lastIndexOf("\""));
                System.out.println("qop: " + qop);
            }
            if (strAuthInfo[i].contains("realm")) {
                realm = strAuthInfo[i].substring(strAuthInfo[i].indexOf("\"") + 1, strAuthInfo[i].lastIndexOf("\""));
                System.out.println("realm: " + realm);
            }
            if (strAuthInfo[i].contains("nonce")) {
                nonce = strAuthInfo[i].substring(strAuthInfo[i].indexOf("\"") + 1, strAuthInfo[i].lastIndexOf("\""));
                System.out.println("nonce: " + nonce);
            }
        }
        //设备用户名和密码
        String A1 = username + ":" + realm + ":" + password;
        String A2 = method + ":" + url;
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bMD5_A1 = md5.digest(A1.getBytes());
        String szMD5_A1 = Hex.encodeHexString(bMD5_A1);
        byte[] bMD5_A2 = md5.digest(A2.getBytes());
        String szMD5_A2 = Hex.encodeHexString(bMD5_A2);
        String nc = "00000001";
        String cnonce = UUID.randomUUID().toString().replaceAll("-", "");
        String resContent = szMD5_A1 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + szMD5_A2;
        byte[] bMD5_Content = md5.digest(resContent.getBytes());
        String response = Hex.encodeHexString(bMD5_Content);
        return "Digest username=\"admin\", "
                + "realm=\"" + realm + "\", "
                + "nonce=\"" + nonce + "\", "
                + "uri=\"" + url + "\", "
                + "algorithm=\"MD5\", "
                + "qop=" + qop + ", "
                + "nc=" + nc + ", "
                + "cnonce=\"" + cnonce + "\", "
                + "response=\"" + response + "\"";
    }


    /**
     * socket实现表单数据上传
     *
     * @param Method
     * @param szAddress
     * @param URL
     * @param byBodyParam
     * @param bAuth
     * @param AuthInfo
     * @return
     * @throws Exception
     */
    public static String httpSocket(String Method, String szAddress, String URL, String szBoundary, byte[] byBodyParam, boolean bAuth, String AuthInfo) throws Exception {
        java.net.URL httpurl = new URL(szAddress + URL);
        HttpURLConnection httpConn = (HttpURLConnection) httpurl.openConnection();
        //表单格式发送数据
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + szBoundary);
        if (bAuth) {
            httpConn.setRequestProperty("Authorization", AuthInfo); //带上认证字段
        }
        httpConn.setRequestMethod(Method);
        httpConn.setDoInput(true);
        httpConn.setDoOutput(true);
        httpConn.setConnectTimeout(20000);
        httpConn.setReadTimeout(20000);
        httpConn.setUseCaches(false);
        httpConn.connect();

        OutputStream outputData = httpConn.getOutputStream();
        outputData.write(byBodyParam); //输入表单数据
        outputData.flush();
        outputData.close();

        String authorization = "";
        if (httpConn.getResponseCode() == 200) {
            InputStream inputData = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputData));
            String str;
            System.out.println("输出: ");
            while ((str = reader.readLine()) != null)
                System.out.println(str);
            inputData.close();
        } else if (httpConn.getResponseCode() == 401) {
            authorization = httpConn.getHeaderField("WWW-Authenticate");
        } else {
            System.out.println("getResponseCode: " + httpConn.getResponseCode());
            InputStream errData = httpConn.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(errData));
            System.out.println("输出: ");
            String str;
            while ((str = reader.readLine()) != null)
                System.out.println(str);
            errData.close();
        }
        httpConn.disconnect();

        return authorization;
    }
}

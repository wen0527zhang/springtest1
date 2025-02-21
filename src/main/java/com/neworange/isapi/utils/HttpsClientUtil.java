package com.neworange.isapi.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Https工具类
 */
public class HttpsClientUtil {
    public HttpsClientUtil() throws Exception {

    }

    public static boolean bHttpsEnabled = false;


    //CN:初始化一个HTTPS客户端
    //EN:: Initializes an HTTPS client
    public static void httpsClientInit(String IP, String user, String Password) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        Credentials credentials = new UsernamePasswordCredentials(user, Password);
        credentialsProvider.setCredentials(new AuthScope(IP, 443), credentials);
        HttpsClientUtil.httpsClient = HttpClients.custom().setSSLSocketFactory(HttpsClientUtil.createSSLConnSocketFactory()).setDefaultCredentialsProvider(credentialsProvider).build();
    }

    //EN:Create a secure connection based on SSL/TLS
    //CN:创建基于SSL/TLS协议的安全连接
    public static SSLConnectionSocketFactory createSSLConnSocketFactory() {

        SSLConnectionSocketFactory sslsf = null;
        try {

            //EN:Verify that the server certificate is trusted
            //CN:验证服务器证书是否可信任
            TrustStrategy trustStrategy = new TrustStrategy() {

                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            };

            //EN:Create an SSLContext object that contains the configuration information for the SSL/TLS protocol
            //CN:创建SSLContext对象,包含SSL/TLS协议的配置信息
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, trustStrategy).build();


            //EN:Verify that the host name of the server certificate matches the requested host name
            //CN:验证服务器证书的主机名是否与请求的主机名匹配
            X509HostnameVerifier x509HostnameVerifier = new X509HostnameVerifier() {

                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

                public void verify(String host, SSLSocket ssl) throws IOException {
                }


                public void verify(String host, X509Certificate cert) throws SSLException {
                }

                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                }
            };


            //EN:Create the Socket for the SSL connection
            //CN:创建SSL连接的Socket
            sslsf = new SSLConnectionSocketFactory(sslContext, x509HostnameVerifier);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslsf;
    }


    public static CloseableHttpClient httpsClient = null;

    //EN:The Https GET method returns a response message of string type
    //CN:Https GET方法 返回字符串类型的响应消息
    public static String httpsGet(String url) {
        String Ret = "";
        try {
            CloseableHttpResponse response = null;
            HttpGet httpGet = new HttpGet(url);

            response = httpsClient.execute(httpGet);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Ret = "error " + statusCode;
            }

            //EN:Get response real
            //CN:获取响应实体
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                Ret = "error response is null";
            }
            Ret = EntityUtils.toString(entity, "utf-8");
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Ret;
    }
}

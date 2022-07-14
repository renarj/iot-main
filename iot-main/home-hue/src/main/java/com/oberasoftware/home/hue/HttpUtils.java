package com.oberasoftware.home.hue;

import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpUtils {

    private static final String SSL = "SSL";

    public static URI createUri(HueBridge bridge, String resourcePath) {
        return URI.create("https://" + bridge.getBridgeIp() + "/api/" + bridge.getBridgeToken() + "/" + resourcePath);
    }

    public static HttpClient createClient()  {
        try {
            SSLContext sslContext = SSLContext.getInstance(SSL);

            TrustManager trustAllManager = new X509ExtendedTrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
                }
            };

            TrustManager[] noopTrustManager = new TrustManager[]{
                    trustAllManager
            };

            sslContext.init(null, noopTrustManager, null);

            return HttpClient.newBuilder().sslContext(sslContext).build();
        } catch(KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeIOTException("Could not load Hue Http Client", e);
        }
    }
}

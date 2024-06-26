package com.boha.skunk.services;

import okhttp3.OkHttpClient;
import okhttp3.internal.tls.OkHostnameVerifier;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

@Component
@SuppressWarnings("all")
public class OKHelper {
    private final OkHttpClient client;
    static final String mm = "\uD83E\uDD66\uD83E\uDD66\uD83E\uDD66 OKHelper  \uD83D\uDC9B";
    static final Logger logger = Logger.getLogger(OKHelper.class.getSimpleName());

    public OKHelper() {
        this.client = new OkHttpClient.Builder()
                .sslSocketFactory(createTrustAllSSLSocketFactory(), createTrustAllTrustManager())
                .hostnameVerifier(OkHostnameVerifier.INSTANCE)
                .build();
        logger.info(mm+ " OkHttpClient constructed: " + this.client.toString());
    }

    public OkHttpClient getClient() {
        return client;
    }

    private SSLSocketFactory createTrustAllSSLSocketFactory() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            logger.info(mm+" SSL SocketFactory constructed" );
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create TrustAllSSLSocketFactory", e);
        }
    }

    private X509TrustManager createTrustAllTrustManager() {
        return new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }
}
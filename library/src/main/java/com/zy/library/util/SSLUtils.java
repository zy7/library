package com.zy.library.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLUtils {
    private SSLUtils() {}

    public static class SSLParam {
        public SSLSocketFactory sslSocketFactory;
        public X509TrustManager trustManager;
    }

    public static SSLParam generateSSLFactory(InputStream bksFile, String password, InputStream... certificates) {
        SSLParam params = null;
        try {
            params = new SSLParam();
            TrustManager[] trustManagers = getTrustManagers(certificates);
            KeyManager[] keyManagers = getKeyManager(bksFile, password);
            X509TrustManager trustManager = null;
            SSLContext sslContext = SSLContext.getInstance("TLS");
            if (trustManagers != null) {
                trustManager = new MyTrustManager(getX509TrustManager(trustManagers));
            } else {
                trustManager = new UnSafeTrustManager();
            }
            sslContext.init(keyManagers, new TrustManager[] {trustManager}, null);
            params.sslSocketFactory = sslContext.getSocketFactory();
            params.trustManager = trustManager;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return params;
    }

    public static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
    }

    private static TrustManager[] getTrustManagers(InputStream... certificates) {
        if (certificates == null || certificates.length <= 0)
            return null;

        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore key = KeyStore.getInstance(KeyStore.getDefaultType());
            key.load(null);
            for (int i=0; i<certificates.length; i++) {
                String alias = Integer.toString(i);
                key.setCertificateEntry(alias, certificateFactory.generateCertificate(certificates[i]));
                try {
                    if (certificates[i] != null)
                        certificates[i].close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(key);
            return trustManagerFactory.getTrustManagers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static KeyManager[] getKeyManager(InputStream bksFile, String password) {
        if (bksFile == null || password == null)
            return null;

        try {
            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class UnSafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class MyTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public MyTrustManager(X509TrustManager localTrustManager)
                throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init((KeyStore) null);
            this.defaultTrustManager = getX509TrustManager(factory.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ce) {
                localTrustManager.checkServerTrusted(chain, authType);
            }
        }


        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return new X509Certificate[0];
        }
    }

    private static X509TrustManager getX509TrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager)
                return (X509TrustManager) trustManager;
        }
        return null;
    }
}

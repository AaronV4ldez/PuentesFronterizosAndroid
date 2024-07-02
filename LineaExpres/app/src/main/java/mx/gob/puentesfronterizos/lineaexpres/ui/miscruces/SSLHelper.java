// SSLHelper.java
package mx.gob.puentesfronterizos.lineaexpres.ui.miscruces;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import mx.gob.puentesfronterizos.lineaexpres.R;

public class SSLHelper {
    public static SSLContext getSSLContext(Context context) throws Exception {
        // Cargar tu certificado desde el archivo raw
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = new BufferedInputStream(context.getResources().openRawResource(R.raw.my_cert));
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }

        // Crear un KeyStore e inicializarlo con el certificado de confianza
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Crear un TrustManager que confíe en el certificado en el KeyStore
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        // Crear un contexto SSL con el TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext;
    }
}

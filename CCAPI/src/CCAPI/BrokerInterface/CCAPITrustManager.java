/*
 * Created on Feb 6, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package CCAPI.BrokerInterface;

import javax.security.cert.X509Certificate;


/**
 *
 * This is the CCAPITrustManager. The TrustManager is called to check a server certificate. For those who don't know the procedure:
 * The client connects over SSL, the server sends a certificate, the client checks this cert to verifiy that the server is the server it want's to connect to,
 * the client reacts appropriate.
 * The current implementation is highly unsecure - it accepts every certificate - but server hijacking is really really rare in the current internet - trust a hacker.
 *
 * @author us
 *
 */
public class CCAPITrustManager implements javax.net.ssl.X509TrustManager {
    CCAPITrustManager() {
    }

    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
        String authType) {
    }

    public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
        String authType) {
    }

    public boolean isServerTrusted(X509Certificate[] certs) {
        return true;
    }

    public boolean isClientTrusted(X509Certificate[] certs) {
        return true;
    }
}

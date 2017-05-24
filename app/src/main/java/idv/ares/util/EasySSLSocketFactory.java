package idv.ares.util;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/*
 * @deprecated
 */
@Deprecated
public class EasySSLSocketFactory extends SSLSocketFactory {
	SSLContext sslContext = SSLContext.getInstance("TLS");

	public EasySSLSocketFactory(KeyStore truststore)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
		super(truststore);
		
		TrustManager tm = new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}
		};

		sslContext.init(null, new TrustManager[] { tm }, null);
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
		return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	}

	@Override
	public Socket createSocket() throws IOException {
		return sslContext.getSocketFactory().createSocket();
	}
	
	public static void setHttpsConnect(HttpsURLConnection conn) throws Exception {
//		InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));
//		Certificate ca = loadCA(caInput);
//		KeyStore keyStore = createKeyStore(ca);
//		TrustManager[] trustManagers = createTrustManager(keyStore);
		
		TrustManager[] trustManagers = EasySSLSocketFactory.createTrustManager(null);
		// Create an SSLContext that uses our TrustManager 
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, trustManagers, null); 
	
		HostnameVerifier hostnameVerifier = new HostnameVerifier() {
		    @Override 
		    public boolean verify(String hostname, SSLSession session) {
		        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
		        //return hv.verify("nanocosm.com", session);
		        return true;	// trust everyone for now
		    }
		}; 
		
		conn.setHostnameVerifier(hostnameVerifier);
		conn.setSSLSocketFactory(context.getSocketFactory());
	}
	
	public static void setHttpsConnectEx(HttpsURLConnection conn) throws Exception {
//		InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));
//		Certificate ca = loadCA(caInput);
//		KeyStore keyStore = createKeyStore(ca);
//		TrustManager[] trustManagers = createTrustManager(keyStore);
		
		TrustManager[] trustManagers = EasySSLSocketFactory.createTrustManager(null);
		// Create an SSLContext that uses our TrustManager 
		SSLContext context = SSLContext.getInstance("TLSv1.1");
		context.init(null, trustManagers, null); 
	
		HostnameVerifier hostnameVerifier = new HostnameVerifier() {
		    @Override 
		    public boolean verify(String hostname, SSLSession session) {
		        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
		        //return hv.verify("nanocosm.com", session);
		        return true;	// trust everyone for now
		    }
		}; 
		
		//conn.setHostnameVerifier(hostnameVerifier);
		conn.setSSLSocketFactory(context.getSocketFactory());
	}	
	
	private static TrustManager[] createTrustManager(KeyStore keyStore) throws KeyStoreException, NoSuchAlgorithmException {
		// TODO-Ares create trust manager by keysotre
//		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//		tmf.init(keyStore);
//		return tmf.getTrustManagers();
		
		return new TrustManager[] { new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[] {};
			}
		} };
	}
	
	private KeyStore createKeyStore(Certificate ca) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(null, null);
		keyStore.setCertificateEntry("ca", ca);
		return keyStore;
	}
	
	private  Certificate loadCA(InputStream caInput) throws CertificateException, IOException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		Certificate ca; 
		try { 
		    ca = cf.generateCertificate(caInput);
		    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
		} finally { 
		    caInput.close();
		}
		return ca;
	}
}

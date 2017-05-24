package idv.ares.net.restapi.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public abstract class RestMethod {
	protected static final String HTTPS = "https";
	
	protected String url;
	protected Map<String, List<String>> headers;
	protected String body;
	
	public RestMethod(String url, Map<String, List<String>> headers, String body) {
		this.url = url;
		this.headers = headers;
		this.body = body;
	}
	
	public abstract Response execute();
	
	protected void setHttpHeaders(HttpURLConnection conn, Map<String, List<String>> headers) {
		if (conn != null && headers != null && headers.size() > 0) {
			for (String header : headers.keySet()) {
				for (String value : headers.get(header)) {
					conn.addRequestProperty(header, value);
				}
			}
		}
	}
	
	protected void setHttpOutput(HttpURLConnection conn, String body) throws IOException {
		if (body != null && !body.isEmpty()) {
			byte[] payload = body.getBytes(Charset.defaultCharset());
			conn.setDoOutput(true);
			conn.setFixedLengthStreamingMode(payload.length);
			conn.getOutputStream().write(payload);
		}		
	}
	
	protected String readStream(InputStream in) throws IOException {
		InputStreamReader isr = new InputStreamReader(in, Charset.defaultCharset());
		BufferedReader reader = new BufferedReader(isr, 8);
		StringBuilder result = new StringBuilder();
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			result.append(line);
        }
		
		return result.toString();
	}
	
	protected boolean isSSLProtocol(URI url) {
		return url.getScheme().equalsIgnoreCase(HTTPS);
	}

	/**
	 * 
	 * @return to return a Http URL Connection object for http or https connection
	 * @throws Exception 
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	protected HttpURLConnection getHttpConnection(String strURL) throws URISyntaxException, IOException, Exception {
		HttpURLConnection conn;
		
		URL url = new URL(strURL);
		
		if (url.toURI().getScheme().equalsIgnoreCase(HTTPS)) {
			conn = (HttpsURLConnection) url.openConnection();
			
			TrustManager[] trustManagers = createTrustManager(null);
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
			
			((HttpsURLConnection)conn).setHostnameVerifier(hostnameVerifier);
			((HttpsURLConnection)conn).setSSLSocketFactory(context.getSocketFactory());
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}
		
		return conn;
	}
	
	private TrustManager[] createTrustManager(KeyStore keyStore) throws KeyStoreException, NoSuchAlgorithmException {
		// TODO [Ares] create trust manager by keystore
//		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//		tmf.init(keyStore);
//		return tmf.getTrustManagers();
		return new TrustManager[] { 
			new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];	// { };
				}
			}
		}; 
	}

}

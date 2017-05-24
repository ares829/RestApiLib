package idv.ares.net.restapi.rest;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import idv.ares.net.restapi.rest.RestMethodFactory.Method;
import idv.ares.util.Logger;

class MethodGet extends RestMethod {
	private static String TAG = MethodGet.class.getSimpleName();
	
	public MethodGet(Method method, String url, Map<String, List<String>> headers, String body) {
		super(url, headers, body);
	}

	@Override
	public Response execute() {
		HttpURLConnection conn = null;
		Response response = null;
		int status = -1;
		String exceptionMessage = null;
		
		try {
			Logger.i(TAG, "Execute REST API: " + this.url);
/*			
			URL url = new URL(this.url);		
			if (isSSLProtocol(url.toURI())) {
				conn = (HttpsURLConnection) url.openConnection();
				EasySSLSocketFactory.setHttpsConnect((HttpsURLConnection) conn);
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}
*/			
			conn = getHttpConnection(this.url);
			
			// Sets the maximum time 30 seconds to wait while connecting and read.
			// Maybe can revise that UI layer can decide this time out value.
			conn.setConnectTimeout(RestMethodFactory.getInstance().getConnectTimeout());
			conn.setReadTimeout(RestMethodFactory.getInstance().getReadTimeout());
			conn.setRequestMethod("GET");
			
			setHttpHeaders(conn, headers);	
			conn.setDoOutput(false);

			status = conn.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_CREATED) {
				response = new Response(status, readStream(conn.getInputStream()));
			} else {
				response = new Response(status, null);
				response.setExceptionMessage(readStream(conn.getErrorStream()));
			}

		} catch (Exception e) {
			exceptionMessage = e.getMessage();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
			conn = null;
		}
		
		if (response == null) {
			response = new Response(status, null);
			response.setExceptionMessage(exceptionMessage);
		}
		
		return response;
	}
	
}

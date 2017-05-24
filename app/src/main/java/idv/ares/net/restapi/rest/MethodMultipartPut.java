package idv.ares.net.restapi.rest;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import idv.ares.net.restapi.rest.RestMethodFactory.Method;
import idv.ares.util.Logger;

public class MethodMultipartPut extends RestMethod {
	private static String TAG = MethodMultipartPut.class.getSimpleName();
	
	public static final String BOUNDARY = "Live365_boundary";

	public MethodMultipartPut(Method method, String url, Map<String, List<String>> headers, String body) {
		super(url, headers, body);
	}

	@Override
	public Response execute() {
		HttpURLConnection conn = null;
		Response response = null;
		int status = -1;
		String exceptionMessage = null;

		FileInputStream fileInputStream = null;
		final String HYPHENS = "--";
		final String CRLF = "\r\n";

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
			setHttpHeaders(conn, headers);
			conn.setRequestMethod("PUT");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);

			// =======================================================================================
			String fileType = body.substring(body.lastIndexOf("."));
			String strContentDisposition = "Content-Disposition: form-data; name=\"file\"; filename=\"profileImg" + fileType + "\"";
			String strContentType = "Content-Type: image/jpeg";

			DataOutputStream dataOS = new DataOutputStream(conn.getOutputStream());
			dataOS.writeBytes(HYPHENS + BOUNDARY + CRLF);
			dataOS.writeBytes(strContentDisposition + CRLF);
			dataOS.writeBytes(strContentType + CRLF);
			dataOS.writeBytes(CRLF);

			fileInputStream = new FileInputStream(new File(body));
			int iBytesAvailable = fileInputStream.available();
			byte[] byteData = new byte[iBytesAvailable];
			int iBytesRead = fileInputStream.read(byteData, 0, iBytesAvailable);
			while (iBytesRead > 0) {
				dataOS.write(byteData, 0, iBytesAvailable);
				iBytesAvailable = fileInputStream.available();
				iBytesRead = fileInputStream.read(byteData, 0, iBytesAvailable);
			}

			fileInputStream.close();

			dataOS.writeBytes(CRLF);
			dataOS.writeBytes(HYPHENS + BOUNDARY + HYPHENS);
			dataOS.flush();
			dataOS.close();
			// =======================================================================================
			
			status = conn.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_CREATED) {
				response = new Response(status, readStream(conn.getInputStream()));
			} else {
				response = new Response(status, null);
				response.setExceptionMessage(readStream(conn.getErrorStream()));
			}

		} catch (Exception e) {
			exceptionMessage = e.getMessage();
			e.printStackTrace();
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

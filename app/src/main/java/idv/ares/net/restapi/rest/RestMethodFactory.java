package idv.ares.net.restapi.rest;

import java.util.List;
import java.util.Map;

public class RestMethodFactory {

	private static RestMethodFactory mInstance;
	private static Object mLock = new Object();
	
//	public static final int RESOURCE_STATIONS = 1;
	public static final int RESOURCE_CREAT_MEMBER_ACCOUNT = 2;
	public static final int RESOURCE_CREAT_ACCESS_TOKEN = 3;
	
	private int mConnectionTimeout = 30 * 1000;
	private int mReadTimeout = 30 * 1000;
	
	public static enum Method {
		GET, POST, PUT, DELETE, MULTIPART_PUT
	}
	
	public static RestMethodFactory getInstance() {
		synchronized (mLock) {
			if (mInstance == null) {
				mInstance = new RestMethodFactory();
			}
		}

		return mInstance;
	}

	public RestMethod getRestMethod(Method method, String url, Map<String, List<String>> headers, String body) {
		/*
		if (method == Method.GET || method == Method.POST) {
			return new RestMethod(method, url, headers, body);
		}		
		return null;
		*/
		RestMethod requestMethod = null;
		
		switch (method) {
		case GET:
			requestMethod = new MethodGet(method, url, headers, body);
			break;
			
		case POST:
			requestMethod = new MethodPost(method, url, headers, body);
			break;
			
		case PUT:
			requestMethod = new MethodPut(method, url, headers, body);
			break;
			
		case MULTIPART_PUT:
			requestMethod = new MethodMultipartPut(method, url, headers, body);
			break;
			
		case DELETE:
			requestMethod = new MethodDelete(method, url, headers, body);
			break;
			
		default:
			break;
			
		}
		
		return requestMethod;
	}
	
	public void setConnectTimeout(int timeoutMillis) {
		mConnectionTimeout = timeoutMillis;
	}
	
	public void setReadTimeout(int timeoutMillis) {
		mReadTimeout = timeoutMillis;
	}
	
	public int getConnectTimeout() {
		return mConnectionTimeout;
	}
	
	public int getReadTimeout() {
		return mReadTimeout;
	}
	
}

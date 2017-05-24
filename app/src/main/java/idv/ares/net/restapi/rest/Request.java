package idv.ares.net.restapi.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import idv.ares.net.restapi.rest.RestMethodFactory.Method;

@Deprecated
public class Request {
	
	private String mRequestUrl = null;
	private Map<String, List<String>> mHeaders = null;
	private Method mMethod = Method.GET;
	private String body;
	
	public Request(Method method, String requestUrl, Map<String, List<String>> headers, String body) {
		this.mMethod = method;
		this.mRequestUrl = requestUrl;
		this.mHeaders = headers;
		this.body = body;
	}
	
	public Method getMethod() {
		return mMethod;
	}

	public String getRequestUrl() {
		return mRequestUrl;
	}

	public Map<String, List<String>> getHeaders() {
		return mHeaders;
	}

	public void addHeader(String key, List<String> value) {
		if (mHeaders == null) {
			mHeaders = new HashMap<String, List<String>>();
		}
		
		mHeaders.put(key, value);
	}

	public String getBody() {
		return body;
	}
}

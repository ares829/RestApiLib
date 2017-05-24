package idv.ares.net.restapi.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.google.gson.JsonSyntaxException;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

import idv.ares.net.restapi.rest.Response;
import idv.ares.net.restapi.rest.RestMethod;
import idv.ares.net.restapi.rest.RestMethodFactory;
import idv.ares.net.restapi.rest.RestMethodFactory.Method;
import idv.ares.util.Logger;

public class RestfulApiService extends IntentService {
	private static final String TAG = RestfulApiService.class.getSimpleName();
	
	public static final String EXTRA_METHOD = "EXTRA_METHOD";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_MULTIPART_PUT = "MULTIPART_PUT";
	public static final String METHOD_DELETE = "DELETE";
	
	public static final String EXTRA_URL = "EXTRA_URL";
	public static final String EXTRA_CLASS = "EXTRA_CLASS";
	public static final String EXTRA_JSON_BODY = "EXTRA_JSON_BODY";
 
	public static final String EXTRA_SERVICE_CALLBACK = "EXTRA_SERVICE_CALLBACK";
	
	public static final String EXTRA_REQUEST_INTENT = "EXTRA_REQUEST_INTENT";
	public static final String EXTRA_RESULT = "EXTRA_RESULT";
	public static final String EXTRA_HEADERS = "EXTRA_HEADERS";
	
	public static final int RESULT_CODE_RESOURCE_VALIDATE = 0;
	public static final int RESULT_CODE_RESOURCE_INVALIDATE = -1;
	public static final int RESULT_CODE_REQUEST_INVALIDATE = -2;
	
	private Intent requestIntent;
	private ResultReceiver resultReceiver;
	
	public RestfulApiService() {
		super("RestfulApiService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		requestIntent = intent;
		
		String method = requestIntent.getStringExtra(EXTRA_METHOD);
		resultReceiver = requestIntent.getParcelableExtra(EXTRA_SERVICE_CALLBACK);
		String url = requestIntent.getStringExtra(EXTRA_URL);
		Serializable serializable = requestIntent.getSerializableExtra(EXTRA_CLASS);
		RestMethodResult<?> result = requestIntent.getParcelableExtra(EXTRA_RESULT);
		HashMap<String, List<String>> headers = (HashMap<String, List<String>>) requestIntent.getSerializableExtra(EXTRA_HEADERS);
		Class<?> cls = (Class<?>) serializable;
		
		//int resultCode = RESULT_CODE_RESOURCE_VALIDATE;
		if (isMethodSupported(method)) {
			Logger.d(TAG, "request id: " + requestIntent.getLongExtra(ServiceHelper.REQUEST_ID, -1) + " | URL: " + url);

			String jsonBody = requestIntent.getStringExtra(EXTRA_JSON_BODY);	
			Method httpMethod = Method.valueOf(method);
			RestMethod restMethod = RestMethodFactory.getInstance().getRestMethod(httpMethod, url, headers, jsonBody);
			
			// [Ares] temporary for test delay 10 seconds to send a request.
//			try {
//				Thread.sleep(10000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			Response response = restMethod.execute();
			
			callbackHandler(resultReceiver, response, result, cls);		
			
		} else {
			handleUnsupportedMethod(resultReceiver, result);
			
		}

	}

	private void handleUnsupportedMethod(ResultReceiver resultReceiver, RestMethodResult<?> result) {
		if (resultReceiver != null) {
			int resultCode = RESULT_CODE_RESOURCE_VALIDATE;
			// not define request method so that it is used for testing
			try {
				result.setStatusOK(true);
				resultCode = RESULT_CODE_RESOURCE_VALIDATE;
			} catch (Exception e) {
				result.setStatusOK(false);
				result.setErrorMessage(e.getMessage());
				resultCode = RESULT_CODE_RESOURCE_INVALIDATE;
			}
			
			Bundle bundle = getOriginalIntentAsBundle();
			if (bundle != null) {
				bundle.putParcelable(EXTRA_RESULT, result);
			}
			
			resultReceiver.send(resultCode, bundle);
		}
	}
	
	private boolean isMethodSupported(String method) {
		return method.equalsIgnoreCase(METHOD_GET) || method.equalsIgnoreCase(METHOD_POST) || method.equalsIgnoreCase(METHOD_DELETE)
				|| method.equalsIgnoreCase(METHOD_PUT) || method.equalsIgnoreCase(METHOD_MULTIPART_PUT);
	}
	
	private void callbackHandler(ResultReceiver resultReceiver, Response response, RestMethodResult<?> result, Class<?> cls) {
		if (response != null && resultReceiver != null) {
			int resultCode = RESULT_CODE_RESOURCE_VALIDATE;
			String body = "";
			if (response.getStatus() == HttpURLConnection.HTTP_OK || response.getStatus() == HttpURLConnection.HTTP_CREATED) {
				try {
					body = response.getBody();
					result.setResultType(cls);
					result.setResponse(body);

					result.setStatusOK(true);
					resultCode = RESULT_CODE_RESOURCE_VALIDATE;

				} catch (JsonSyntaxException ex) {
					result.setStatusOK(false);
					result.setErrorMessage(ex.getMessage());
					resultCode = RESULT_CODE_RESOURCE_INVALIDATE;
					
				}
			} else {
				result.setStatusOK(false);
				result.setErrorMessage(response.getExceptionMessage());
				resultCode = RESULT_CODE_RESOURCE_INVALIDATE;
				
			}

			Bundle bundle = getOriginalIntentAsBundle();
			if (bundle != null) {
				bundle.putParcelable(EXTRA_RESULT, result);
			}

			Logger.d(TAG, "status : " + response.getStatus());
			result.setStatusCode(response.getStatus());
			
			//Log.w(TAG, "request : " + url);
			if (result.isStatusOK()) {
				Logger.d(TAG, "request id: " + requestIntent.getLongExtra(ServiceHelper.REQUEST_ID, -1) + " | response: " + body);
			} else {
				Logger.d(TAG, "request id: " + requestIntent.getLongExtra(ServiceHelper.REQUEST_ID, -1) + " | response : " + result.getErrorMessage());
			}
			
			resultReceiver.send(resultCode, bundle);			
		}
	}
	
	protected Bundle getOriginalIntentAsBundle() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(EXTRA_REQUEST_INTENT, requestIntent);
		return bundle;
	}
	
}

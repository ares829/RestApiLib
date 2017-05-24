package idv.ares.net.restapi.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import idv.ares.net.restapi.rest.RestMethodFactory;
import idv.ares.util.Logger;

public class ServiceHelper {
	private static final String TAG = ServiceHelper.class.getSimpleName();
	
	protected static final String REQUEST_ID = "REQUEST_ID";
	protected Context mContext = null;
	/* TODO: review and to clean up 
	 *  Moved to here to keep compiler silence
	 *  should be controlled/provided by client
	 */
//	protected DomainConfig.Domain targetDomain;
//	protected String apiHost;
	
	public ServiceHelper(Context context) {
		mContext = context;
//		targetDomain = DomainConfig.getTargetDomain(mContext);
	}
	
	protected <T> Intent doGet(String url, HashMap<String, List<String>> headers, Class<T> cls, ResponseCallback<T> respCallback) {
		Intent intent = createIntent(RestfulApiService.METHOD_GET, url, headers, cls, respCallback);
		execute(intent);
		return intent;
	}
	
	protected <PT, T> Intent doPost(String url, HashMap<String, List<String>> headers, PT body, Class<T> cls, ResponseCallback<T> respCallback) {
		Intent intent = createIntent(RestfulApiService.METHOD_POST, url, headers, cls, respCallback);
		
		if(body != null) {
			intent.putExtra(RestfulApiService.EXTRA_JSON_BODY, new Gson().toJson(body, body.getClass()));
		}
		
		execute(intent);
		return intent;
	}
	
	protected <PT, T> Intent doPut(String url, HashMap<String, List<String>> headers, PT body, Class<T> cls, ResponseCallback<T> respCallback) {
		Intent intent = createIntent(RestfulApiService.METHOD_PUT, url, headers, cls, respCallback);
		
		if(body != null) {
			intent.putExtra(RestfulApiService.EXTRA_JSON_BODY, new Gson().toJson(body, body.getClass()));
		}
		
		execute(intent);
		return intent;
	}
	
	protected <T> Intent doMultipartPut(String url, HashMap<String, List<String>> headers, String filePath, Class<T> cls, ResponseCallback<T> respCallback) {
		Intent intent = createIntent(RestfulApiService.METHOD_MULTIPART_PUT, url, headers, cls, respCallback);
		
		if(!TextUtils.isEmpty(filePath)) {
			intent.putExtra(RestfulApiService.EXTRA_JSON_BODY, filePath);
		}
		
		execute(intent);
		return intent;
	}
	
	protected <T> Intent doDelete(String url, HashMap<String, List<String>> headers, Class<T> cls, ResponseCallback<T> respCallback) {
		Intent intent = createIntent(RestfulApiService.METHOD_DELETE, url, headers, cls, respCallback);
		execute(intent);
		return intent;
	}
	
	protected <T> Intent doTest(Class<T> cls, String jsonData, ResponseCallback<T> respCallback) {
		Intent intent = createIntent("", "", null, cls, respCallback);
		RestMethodResult<T> result = intent.getParcelableExtra(RestfulApiService.EXTRA_RESULT);
		result.setResponse(jsonData);
		execute(intent);

		return intent;
	}

	protected long getRequestId(Intent intent) {
        return intent.getLongExtra(REQUEST_ID, 0);
    }
	
	private void execute(Intent intent) {
		if(mContext != null) {
			mContext.startService(intent);
		} else {
			// TODO: any chance to occur?
			Logger.w(TAG, "execute() - context is null.");
		}
	}
	
	private <T> Intent createIntent(String method, String url, HashMap<String, List<String>> headers, Class<T> cls, final ResponseCallback<T> respCallback) {
		ResultReceiver resultReceiver = new ResultReceiver(null) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				handleResponse(respCallback, resultCode, resultData);
			}
		};
		
		RestMethodResult<T> result = new RestMethodResult<T>();
		
		long requestId = generateRequestID();
		
		Intent intent = new Intent(mContext, RestfulApiService.class);
		intent.putExtra(REQUEST_ID, requestId);
		intent.putExtra(RestfulApiService.EXTRA_METHOD, method);
		intent.putExtra(RestfulApiService.EXTRA_URL, url);
		intent.putExtra(RestfulApiService.EXTRA_HEADERS, headers);
		intent.putExtra(RestfulApiService.EXTRA_SERVICE_CALLBACK, resultReceiver);
		intent.putExtra(RestfulApiService.EXTRA_CLASS, cls);
		intent.putExtra(RestfulApiService.EXTRA_RESULT, result);
		
		return intent;
	}
	
	private long generateRequestID() {
		return UUID.randomUUID().getLeastSignificantBits();
	}
	
	private <T> void handleResponse(final ResponseCallback<T> respCallback, int resultCode, Bundle resultData) {
		if(resultData == null) {
			Logger.w(TAG, "handleResponse() - result data is null.");
			return;
		}
		
		Intent origIntent = (Intent) resultData.getParcelable(RestfulApiService.EXTRA_REQUEST_INTENT);
		if(origIntent == null) {
			Logger.w(TAG, "handleResponse() - origIntent is null.");
			return;
		}
		
		RestMethodResult<T> result = resultData.getParcelable(RestfulApiService.EXTRA_RESULT);
		Serializable serializable = origIntent.getSerializableExtra(RestfulApiService.EXTRA_CLASS);
		Class<T> cls = (Class<T>) serializable;
		result.setResultType(cls);

		long requestId = origIntent.getLongExtra(REQUEST_ID, 0);
		if (respCallback != null) {
			if (result.isStatusOK()) {
				respCallback.success(requestId, result);
			} else {
				respCallback.fail(requestId, result);
			}
		} else {
			Logger.w(TAG, "handleResponse() - respCallback is null.");
		}
	}
	
	// No needed?  Any flexibility do we buy from this the binding mechanism?
/*	
	public static <I> I newInstance(Context context, Class<? extends ServiceHelper<I>> cls) throws Exception {
		APIServiceProxy proxy = new APIServiceProxy();
		return (I) proxy.bind(context, cls.getDeclaredConstructor(Context.class).newInstance(context));
	}
*/	
//	public static void setTargetDomain(String name, boolean isPublic, int type, String playHost, String commandHost, String apiHost, String secureApiHost) {
//		targetDomain = DomainConfig.setTargetDomain(name, isPublic, type, playHost, commandHost, apiHost, secureApiHost);
//	}
	
	public static void setConnectTimeout(int timeoutMillis) {
		RestMethodFactory.getInstance().setConnectTimeout(timeoutMillis);
	}
	
	public static void setReadTimeout(int timeoutMillis) {
		RestMethodFactory.getInstance().setReadTimeout(timeoutMillis);
	}
}

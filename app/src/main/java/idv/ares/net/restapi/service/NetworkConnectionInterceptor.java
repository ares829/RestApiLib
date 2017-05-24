package idv.ares.net.restapi.service;

import android.content.Context;

import java.lang.reflect.Method;

import idv.ares.util.Logger;
import idv.ares.util.Utils;

public class NetworkConnectionInterceptor extends ApiInterceptor {
	
	private Context ctx;
	
	public NetworkConnectionInterceptor(Context ctx) {
		this.ctx = ctx;
	}

	@Override
	public boolean before(Object proxy, Method method, Object[] args) {
		Logger.d(proxy.getClass().getSimpleName(), "method starts..." + method);

		return Utils.isNetworkConnected(ctx);
	}

	@Override
	public void after(Object proxy, Method method, Object[] args, Boolean invoked) {
		Logger.d(proxy.getClass().getSimpleName(), "method ends..." + method);
	}

	@Override
	public void afterThrowing(Object proxy, Method method, Object[] args, Throwable throwable) {
		Logger.e(proxy.getClass().getSimpleName(), "invoke exception:" + throwable.toString());
	}

	@Override
	public void afterFinally(Object proxy, Method method, Object[] args) {
		Logger.d(proxy.getClass().getSimpleName(), "method afterFinally..." + method); 
	}

}

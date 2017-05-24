package idv.ares.net.restapi.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class APIServiceProxy<T> implements InvocationHandler {

	private T delegate;
	private ApiInterceptor mInterceptor = null;
	
	public T bind(T delegate, ApiInterceptor interceptor) {
        this.delegate = delegate;
        
        mInterceptor = interceptor;
        
        Object object =  Proxy.newProxyInstance(
        		delegate.getClass().getClassLoader(),
        		delegate.getClass().getInterfaces(),
        		this);
		return (T)object;
    } 
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;

		Boolean invoked = mInterceptor.before(delegate, method, args);
		try {
			if(invoked) {
				result = method.invoke(delegate, args);
			}

			mInterceptor.after(delegate, method, args, invoked);
            
    	} catch (Throwable throwable) {
    		mInterceptor.afterThrowing(delegate, method, args, throwable);
    	} finally {
    		mInterceptor.afterFinally(delegate, method, args);
    	}
        
        return result; 
	}

}

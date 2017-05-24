package idv.ares.net.restapi.service;

import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class ApiInterceptor<T> {
    private ArrayList<ApiInterceptorObserver> mObserverList = new ArrayList<ApiInterceptorObserver>();

    abstract public boolean before(Object proxy, Method method, Object[] args);
    abstract public void after(Object proxy, Method method, Object[] args, Boolean invoked);
    abstract public void afterThrowing(Object proxy, Method method, Object[] args, Throwable throwable);
    abstract public void afterFinally(Object proxy, Method method, Object[] args);

    public void registerObserver(ApiInterceptorObserver observer) {
        if (!mObserverList.contains(observer)) {
            mObserverList.add(observer);
        }
    }

    public void unregisterObserver(ApiInterceptorObserver observer) {
        if (mObserverList.contains(observer)) {
            mObserverList.remove(observer);
        }
    }

    public void notifyObservers(T t) {
        for (ApiInterceptorObserver<T> observer : mObserverList) {
            if(observer != null) {
                observer.notifyUpdate(t);
            }
        }
    }

}

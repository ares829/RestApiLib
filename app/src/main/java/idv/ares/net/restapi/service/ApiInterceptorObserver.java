package idv.ares.net.restapi.service;

public interface ApiInterceptorObserver<T> {
    public void notifyUpdate(T message);
}

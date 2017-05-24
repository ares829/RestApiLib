package idv.ares.net.restapi.service;

public interface ResponseCallback<T> {
	void success(long requestId, RestMethodResult<T> result);
	void fail(long requestId, RestMethodResult<T> result);
}

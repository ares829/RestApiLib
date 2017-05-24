package idv.ares.net.restapi.service;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class RestMethodResult<T> implements Parcelable {

	private boolean statusOK = true;
	private int statusCode = -1;
	private String errorMessage;
	private String response;

	private Class<T> cls;

	public RestMethodResult() {
	}

	public T getResult() {
		if(cls == null || response == null) {
			return null;
		}
		if (cls == String.class) {
			return (T) new Gson().fromJson(("\"" + response + "\""), cls);
		}
		return (T) new Gson().fromJson(response, cls);
	}
	
	void setResponse(String response) {
		this.response = response;
	}
	
	void setResultType(Class<?> cls) {
		this.cls = (Class<T>) cls;
	}

	public boolean isStatusOK() {
		return statusOK;
	}

	public void setStatusOK(boolean isStatusOK) {
		this.statusOK = isStatusOK;
	}
	
	public void setStatusCode(int code) {
		statusCode = code;
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	private RestMethodResult(Parcel source) {
		statusOK = source.readByte() != 0;
		statusCode = source.readInt();
		errorMessage = source.readString();

		response = source.readString();
	}

	public static final Creator<RestMethodResult<?>> CREATOR = new Creator<RestMethodResult<?>>() {

		@Override
		public RestMethodResult<?> createFromParcel(Parcel source) {
			return new RestMethodResult(source);
		}

		@Override
		public RestMethodResult<?>[] newArray(int size) {
			return new RestMethodResult[size];
		}
		
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte((byte) (statusOK ? 1 : 0)); 
		dest.writeInt(statusCode);
		dest.writeString(errorMessage);
		dest.writeString(response);
	}
}

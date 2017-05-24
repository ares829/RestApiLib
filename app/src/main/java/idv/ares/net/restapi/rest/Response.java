package idv.ares.net.restapi.rest;

import android.os.Parcel;
import android.os.Parcelable;

public class Response implements Parcelable {
    
    private int httpStatus;
    private String body = null;
    private String exceptionMessage = null;
    
    protected Response(int status, String body) {
        this.httpStatus = status;
        this.body = body;
    }

	private Response(Parcel source) {
		httpStatus = source.readInt();
		body = source.readString();
		exceptionMessage = source.readString();
	}

	public int getStatus() {
		return httpStatus;
	}

	public String getBody() {
		return body;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public static final Creator<Response> CREATOR = new Creator<Response>() {

		@Override
		public Response createFromParcel(Parcel source) {
			return new Response(source);
		}

		@Override
		public Response[] newArray(int size) {
			return new Response[size];
		}
		
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(httpStatus);
		dest.writeString(body);
		dest.writeString(exceptionMessage);
	}
	
}


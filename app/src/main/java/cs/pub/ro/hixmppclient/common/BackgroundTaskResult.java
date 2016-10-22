package cs.pub.ro.hixmppclient.common;

import android.os.Parcel;
import android.os.Parcelable;

public class BackgroundTaskResult implements Parcelable{

    private boolean success;
    private String message;

    public BackgroundTaskResult() {

    }

    private BackgroundTaskResult(Parcel in) {
        success = in.readByte() != 0;
        message = in.readString();
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static final Creator<BackgroundTaskResult> CREATOR = new Creator<BackgroundTaskResult>() {
        @Override
        public BackgroundTaskResult createFromParcel(Parcel in) {
            return new BackgroundTaskResult(in);
        }

        @Override
        public BackgroundTaskResult[] newArray(int size) {
            return new BackgroundTaskResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
    }
}
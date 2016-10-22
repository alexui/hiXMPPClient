package cs.pub.ro.hixmppclient.common;

import android.os.Parcel;
import android.os.Parcelable;

public class ServerStatus implements Parcelable{

    private int connectedClients;

    protected ServerStatus(Parcel in) {
        connectedClients = in.readInt();
    }

    public static final Creator<ServerStatus> CREATOR = new Creator<ServerStatus>() {
        @Override
        public ServerStatus createFromParcel(Parcel in) {
            return new ServerStatus(in);
        }

        @Override
        public ServerStatus[] newArray(int size) {
            return new ServerStatus[size];
        }
    };

    public int getConnectedClients() {
        return connectedClients;
    }

    public void setConnectedClients(int connectedClients) {
        this.connectedClients = connectedClients;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(connectedClients);
    }
}

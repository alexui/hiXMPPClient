package cs.pub.ro.hixmppclient.mainActivity.hiClient.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.Date;
import java.util.Random;

import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.otr.OTRMessageBody;
import cs.pub.ro.hixmppclient.otr.OTRSession;

public class XmppChatMessage implements Parcelable {

    private Gson gson;

    private int id;
    private String subject;
    private String sourceJID;
    private String destinationJID;
    private String body;
    private Date timestamp;

    private boolean clientComposed;

    public XmppChatMessage(String subject, String body, String sourceJID, String destinationJID) {
        this.subject = subject;
        this.body = body;
        this.sourceJID = sourceJID;
        this.destinationJID = destinationJID;
        setMessageId();
    }

    public XmppChatMessage(String subject, String body, String sourceJID, String destinationJID, boolean clientComposed) {
        this.subject = subject;
        this.body = body;
        this.sourceJID = sourceJID;
        this.destinationJID = destinationJID;
        this.clientComposed = clientComposed;
        setMessageId();
    }

    public XmppChatMessage(String subject, String body, String sourceJID, String destinationJID, boolean clientComposed, Date timestamp) {
        this.subject = subject;
        this.body = body;
        this.clientComposed = clientComposed;
        this.timestamp = timestamp;
        this.destinationJID = destinationJID;
        this.sourceJID = sourceJID;
        setMessageId();
    }

    protected XmppChatMessage(Parcel in) {
        id = in.readInt();
        subject = in.readString();
        sourceJID = in.readString();
        destinationJID = in.readString();
        body = in.readString();
        timestamp = (Date) in.readSerializable();
        clientComposed = in.readByte() != 0;
    }

    public static final Creator<XmppChatMessage> CREATOR = new Creator<XmppChatMessage>() {
        @Override
        public XmppChatMessage createFromParcel(Parcel in) {
            return new XmppChatMessage(in);
        }

        @Override
        public XmppChatMessage[] newArray(int size) {
            return new XmppChatMessage[size];
        }
    };

    public String getBody() {
        return body;
    }

    public boolean isClientComposed() {
        return clientComposed;
    }

    public int getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDestinationJID() {
        return destinationJID;
    }

    public String getSourceJID() {
        return sourceJID;
    }

    private void setMessageId() {
        this.id = new Random().nextInt(Constants.MESSAGE_MAX_ID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(subject);
        dest.writeString(sourceJID);
        dest.writeString(destinationJID);
        dest.writeString(body);
        dest.writeSerializable(timestamp);
        dest.writeByte((byte) (clientComposed ? 1 : 0));
    }

    @Override
    public String toString() {
        return "" + body + " \nTS: " + timestamp.toString();
    }

    public byte[] decryptMessageBody(OTRSession otrSession) {

        byte[] key = new byte[0];
        gson = new Gson();
        OTRMessageBody otrMessageBody = gson.fromJson(body, OTRMessageBody.class);
        body = "";

        if (otrMessageBody.checkMessageAuthenticationCode(otrSession)) {
            byte[] decryptedByteArray = otrSession.getAesDecryptedByteArray(otrMessageBody.getContent());
            body = new String(decryptedByteArray);
            key = otrMessageBody.getKey();
        }

        return key;
    }
}

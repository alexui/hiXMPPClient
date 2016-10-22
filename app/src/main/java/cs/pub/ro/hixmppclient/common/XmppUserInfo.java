package cs.pub.ro.hixmppclient.common;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.chat.XmppChatMessage;
import cs.pub.ro.hixmppclient.otr.OTRUtils;

public class XmppUserInfo implements Parcelable, Comparable{

    private boolean available;
    private String jid;
    private String name;
    private ArrayList<String> groups;
    private String statusMode;
    private String statusMessage;

    private byte[] avatar;
    private String lastName;
    private String middleName;
    private String nickName;
    private String organization;
    private String phoneHome;
    private String phoneWork;
    private String emailHome;
    private String emailWork;
    private ArrayList<XmppChatMessage> messages;
    private int unreadMessages;
    private PublicKey publicKeyRSA;
    private PrivateKey privateKeyRSA;

    public XmppUserInfo() {
        messages = new ArrayList<>();
    }

    protected XmppUserInfo(Parcel in) {
        available = in.readByte() != 0;
        jid = in.readString();
        name = in.readString();
        groups = in.createStringArrayList();
        statusMode = in.readString();
        statusMessage = in.readString();
        avatar = in.createByteArray();
        lastName = in.readString();
        middleName = in.readString();
        nickName = in.readString();
        organization = in.readString();
        phoneHome = in.readString();
        phoneWork = in.readString();
        emailHome = in.readString();
        emailWork = in.readString();
        messages = in.readArrayList(XmppChatMessage.class.getClassLoader());
        unreadMessages = in.readInt();
        String publicKeyRsaAsString = in.readString();

        if (!publicKeyRsaAsString.isEmpty()) {
            try {
                publicKeyRSA = OTRUtils.loadPublicKey(publicKeyRsaAsString);
            } catch (GeneralSecurityException e) {
                Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
                Log.e(Constants.LOG_TAG_SMACK, e.toString());
            }
        }
        else publicKeyRSA = null;

        String privateKeyRsaAsString = in.readString();
        if (!privateKeyRsaAsString.isEmpty()) {
            try {
                privateKeyRSA = OTRUtils.loadPrivateKey(privateKeyRsaAsString);
            } catch (GeneralSecurityException e) {
                Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
                Log.e(Constants.LOG_TAG_SMACK, e.toString());
            }
        }
        else privateKeyRSA = null;
    }

    public static final Creator<XmppUserInfo> CREATOR = new Creator<XmppUserInfo>() {
        @Override
        public XmppUserInfo createFromParcel(Parcel in) {
            return new XmppUserInfo(in);
        }

        @Override
        public XmppUserInfo[] newArray(int size) {
            return new XmppUserInfo[size];
        }
    };

    public byte[] getAvatar() {
        return avatar;
    }

    public String getEmailHome() {
        return emailHome;
    }

    public String getEmailWork() {
        return emailWork;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getNickName() {
        return nickName;
    }

    public String getOrganization() {
        return organization;
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public String getPhoneWork() {
        return phoneWork;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getStatusMode() {
        return statusMode;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getJid() {
        return jid;
    }

    public ArrayList<XmppChatMessage> getMessages() {
        return messages;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public PublicKey getPublicKeyRSA() {
        return publicKeyRSA;
    }

    public PrivateKey getPrivateKeyRSA() {
        return privateKeyRSA;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public void setEmailHome(String emailHome) {
        this.emailHome = emailHome;
    }

    public void setEmailWork(String emailWork) {
        this.emailWork = emailWork;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    public void setPhoneWork(String phoneWork) {
        this.phoneWork = phoneWork;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setStatusMode(String statusMode) {
        this.statusMode = statusMode;
    }

    public void setMessages(ArrayList<XmppChatMessage> messages) {
        this.messages = messages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public void setPublicKeyRSA(PublicKey publicKeyRSA) {
        this.publicKeyRSA = publicKeyRSA;
    }

    public void setPublicKeyRSA(String encodedPublicKeyRSA) {
        try {
            publicKeyRSA = OTRUtils.loadPublicKey(encodedPublicKeyRSA);
        } catch (GeneralSecurityException e) {
            Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
            Log.e(Constants.LOG_TAG_SMACK, e.toString());
        }
    }

    public void setPrivateKeyRSA(PrivateKey privateKeyRSA) {
        this.privateKeyRSA = privateKeyRSA;
    }

    @Override
    public int compareTo(Object another) {

        int result;
        XmppUserInfo anotherXmppUserInfo = (XmppUserInfo) another;

        result = jid.compareTo(anotherXmppUserInfo.getJid());

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeString(jid);
        dest.writeString(name);
        dest.writeStringList(groups);
        dest.writeString(statusMode);
        dest.writeString(statusMessage);
        dest.writeByteArray(avatar);
        dest.writeString(lastName);
        dest.writeString(middleName);
        dest.writeString(nickName);
        dest.writeString(organization);
        dest.writeString(phoneHome);
        dest.writeString(phoneWork);
        dest.writeString(emailHome);
        dest.writeString(emailWork);
        dest.writeList(messages);
        dest.writeInt(unreadMessages);

        if (publicKeyRSA != null) {
            try {
                String publicKeyRsaAsString = OTRUtils.savePublicKey(publicKeyRSA);
                dest.writeString(publicKeyRsaAsString);
            } catch (GeneralSecurityException e) {
                Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
                Log.e(Constants.LOG_TAG_SMACK, e.toString());
            }
        }
        else dest.writeString("");

        if (privateKeyRSA != null) {
            try {
                String privateKeyRsaAsString = OTRUtils.savePrivateKey(privateKeyRSA);
                dest.writeString(privateKeyRsaAsString);
            } catch (GeneralSecurityException e) {
                Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
                Log.e(Constants.LOG_TAG_SMACK, e.toString());
            }
        }
        else dest.writeString("");
    }

    @Override
    public boolean equals(Object o) {
        XmppUserInfo otherXmppUserInfo = (XmppUserInfo) o;
        return otherXmppUserInfo.getJid().equals(jid);
    }

    @Override
    public String toString() {
        return " " + jid;
    }

    public void updatePresence(XmppUserInfo userInfo) {

        setAvailable(userInfo.isAvailable());
        setStatusMode(userInfo.getStatusMode());
        setStatusMessage(userInfo.getStatusMessage());
    }

    public void addChatMessage(XmppChatMessage xmppChatMessage) {
        messages.add(xmppChatMessage);
    }

    public void addChatUnreadMessage(XmppChatMessage xmppChatMessage) {
        addChatMessage(xmppChatMessage);
        unreadMessages++;
    }

}

package cs.pub.ro.hixmppclient.common;

import android.os.Parcel;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import cs.pub.ro.hixmppclient.general.Constants;

public class ClientInfo {

    static Map<String, String> hosts = new HashMap<>();

    static {
        hosts.put(Constants.OPENFIRE_SERVER_NAME, Constants.DEFAULT_HOST_ADDRESS);
        hosts.put(Constants.HOT_CHILLI_SERVER_NAME, Constants.DEFAULT_HOST_ADDRESS);
    }

    private boolean connected;
    private boolean authencticated;
    private KeyPair keyPairRSA;

    private String username;
    private String password;
    private String hostName;
    private String hostAddress;

    private String port;
    private String resource;
    private String serviceName;

    public ClientInfo(String username, String password, String hostName) {
        this.username = username;
        this.password = password;
        this.hostName = hostName;
        port = Constants.DEFAULT_PORT;
        resource = Constants.DEFAULT_RESOURCE;
        serviceName = Constants.DEFAULT_XMPP_SERVICE_NAME;
    }

    protected ClientInfo(Parcel in) {
        connected = in.readByte() != 0;
        authencticated = in.readByte() != 0;
        username = in.readString();
        password = in.readString();
        hostName = in.readString();
        hostAddress = in.readString();
        port = in.readString();
        resource = in.readString();
        serviceName = in.readString();
    }

    public String getHostName() {
        return hostName;
    }

    public String getHostAddress() {
        if (hostAddress == null)
            hostAddress = hosts.get(hostName.toLowerCase().replace(" ", ""));
        return hostAddress;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getPort() {
        return port;
    }

    public String getResource() {
        return resource;
    }

    public String getServiceName() {
        return serviceName;
    }

    public boolean isAuthencticated() {
        return authencticated;
    }

    public String getJid() {
        return username + "@" + hostName;
    }

    public boolean isConnected() {
        return connected;
    }

    public KeyPair getKeyPairRSA() {
        return keyPairRSA;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setAuthencticated(boolean authencticated) {
        this.authencticated = authencticated;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public void setKeyPairRSA(KeyPair keyPairRSA) {
        this.keyPairRSA = keyPairRSA;
    }

    @Override
    public int hashCode() {
        return username.hashCode() + password.hashCode() + hostName.hashCode();

    }

    @Override
    public boolean equals(Object o) {
        ClientInfo xmppClientInfo = (ClientInfo) o;
        return xmppClientInfo.username.equals(this.username) &&
                xmppClientInfo.password.equals(this.password) &&
                xmppClientInfo.hostName.equals(this.hostName) &&
                xmppClientInfo.hostAddress.equals(this.hostAddress);
    }

    @Override
    public String toString() {
        return "" +
                username + ", " +
                password + ", " +
                hostName + "( " +
                getHostAddress() + " );";
    }

}

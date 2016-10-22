package cs.pub.ro.hixmppclient.otr;

import cs.pub.ro.hixmppclient.common.XmppUserInfo;

public class OTRSession {

    private boolean alive;

    private XmppUserInfo client;
    private XmppUserInfo user;

    private int clientEncryptionKeyCount;
    private int userEncryptionKeyCount;
    private OTRDiffieHellman diffieHellmanParticipant;

    public OTRSession(XmppUserInfo client, XmppUserInfo user) {
        this.client = client;
        this.user = user;
        clientEncryptionKeyCount = 0;
        userEncryptionKeyCount = 0;
        diffieHellmanParticipant = new OTRDiffieHellman();
    }

    public int geyKeyId() {
        return Integer.valueOf("" + clientEncryptionKeyCount + "0" + userEncryptionKeyCount);
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isAlive() {
        return alive;
    }

    public byte[] createClientEncodedDHPublicKey() {
        return diffieHellmanParticipant.createDHEncodedPublicKey();
    }

    public byte[] getAesDecryptedByteArray(byte[] byteArray) {
        return diffieHellmanParticipant.getAesDecryptedByteArray(byteArray);
    }

    public byte[] getHmacSha1SignedByteArray(byte[] byteArray) {
        return diffieHellmanParticipant.getHmacSha1SignedByteArray(byteArray);
    }

    public byte[] createClientEncodedDHPublicKey(byte[] encodedPublicKeyRequest) {
        return diffieHellmanParticipant.createDHEncodedPublicKey(encodedPublicKeyRequest);
    }

    public void initDHChannel() {
        diffieHellmanParticipant.initDHChannel();
    }

    public void initDHChannel(byte[] encodedPublicKeyResponse) {
        diffieHellmanParticipant.initDHChannel(encodedPublicKeyResponse);
    }

    public byte[] getAesEncryptedByteArray(byte[] byteArray) {
        return diffieHellmanParticipant.getAesEncryptedByteArray(byteArray);
    }

    public byte[] getEncodedAesSecretKey() {
        return diffieHellmanParticipant.getEncodedAesSecretKey();
    }
}

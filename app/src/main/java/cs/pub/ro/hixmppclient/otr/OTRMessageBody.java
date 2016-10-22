package cs.pub.ro.hixmppclient.otr;

import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import cs.pub.ro.hixmppclient.general.Constants;

public class OTRMessageBody {

    private byte[] content;
    private byte[] key;
    private byte[] signature;

    public OTRMessageBody(byte[] content, byte[] key, OTRSession otrSession) {
        this.content = content;
        this.key = key;

        byte[] byteArray = new byte[content.length + key.length];
        System.arraycopy(content, 0, byteArray, 0, content.length);
        System.arraycopy(key, 0, byteArray, content.length, key.length);
        this.signature = otrSession.getHmacSha1SignedByteArray(byteArray);
    }

    public OTRMessageBody(byte[] content, OTRSession otrSession) {
        this.content = content;
        this.key = new byte[0];
        this.signature = otrSession.getHmacSha1SignedByteArray(content);
    }

    public OTRMessageBody(byte[] content, PrivateKey privateKey) {

        this.content = content;
        this.key = content;
        try {
            KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
            keygenerator.init(128);
            SecretKey secretKey = keygenerator.generateKey();
            this.key = OTRUtils.getAesEncryptedByteArray(content, secretKey);
            this.signature = OTRUtils.getRsaEncryptedByteArray(OTRUtils.saveSecretKey(secretKey).getBytes(), privateKey);
        } catch (NoSuchAlgorithmException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            Log.e(Constants.LOG_TAG, e.toString());
        }
    }

    public byte[] getContent() {
        return content;
    }

    public boolean checkMessageAuthenticationCode(OTRSession otrSession) {

        if (signature == null)
            return false;

        byte[] byteArray = new byte[content.length + key.length];
        System.arraycopy(content, 0, byteArray, 0, content.length);
        System.arraycopy(key, 0, byteArray, content.length, key.length);
        byte[] signature = otrSession.getHmacSha1SignedByteArray(byteArray);

        return Arrays.equals(signature, this.signature);
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getKey(PublicKey publicKey) {
        byte[] secretKey = OTRUtils.getRsaDecryptedByteArray(signature, publicKey);
        byte[] bytes = OTRUtils.getAesDecryptedByteArray(key, OTRUtils.loadSecretKey(new String(secretKey)));
        return bytes;
    }

}

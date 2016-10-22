package cs.pub.ro.hixmppclient.otr;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cs.pub.ro.hixmppclient.general.Constants;

public class OTRDiffieHellman {

    private KeyAgreement keyAgreement;
    private SecretKey aesSecretKey;
    private SecretKeySpec hmacSecretKeySpec;
    private PublicKey currentPublicKeyRequest;

    public byte[] createDHEncodedPublicKey() {

        byte[] encodedPublicKey = new byte[0];
        try {
            SecureRandom secureRandom = new SecureRandom();
            BigInteger g = BigInteger.probablePrime(Constants.KEY_LENGTH, secureRandom);
            BigInteger p = BigInteger.probablePrime(Constants.KEY_LENGTH, secureRandom);
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            DHParameterSpec dhSkipParamSpec = new DHParameterSpec(p, g);
            keyPairGenerator.initialize(dhSkipParamSpec);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(keyPair.getPrivate());

            encodedPublicKey = keyPair.getPublic().getEncoded();

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return encodedPublicKey;
    }

    public byte[] createDHEncodedPublicKey(byte[] encodedPublicKeyRequest) {

        byte[] encodedPublicKeyResponse = new byte[0];
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(encodedPublicKeyRequest);
            currentPublicKeyRequest = keyFactory.generatePublic(x509KeySpec);
            DHParameterSpec dhParameterSpec = ((DHPublicKey) currentPublicKeyRequest).getParams();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(dhParameterSpec);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(keyPair.getPrivate());

            encodedPublicKeyResponse = keyPair.getPublic().getEncoded();

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return encodedPublicKeyResponse;
    }

    public void initDHChannel(byte[] encodedPublicKeyResponse) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(encodedPublicKeyResponse);

            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            keyAgreement.doPhase(publicKey, true);
            aesSecretKey = keyAgreement.generateSecret("AES");
            keyAgreement.doPhase(publicKey, true);
            hmacSecretKeySpec = new SecretKeySpec(keyAgreement.generateSecret(), "HmacSHA1");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public void initDHChannel() {
        try {
            keyAgreement.doPhase(currentPublicKeyRequest, true);
            aesSecretKey = keyAgreement.generateSecret("AES");
            keyAgreement.doPhase(currentPublicKeyRequest, true);
            hmacSecretKeySpec = new SecretKeySpec(keyAgreement.generateSecret(), "HmacSHA1");
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public byte[] getAesEncryptedByteArray(byte[] byteArray) {
        byte[] result = new byte[0];

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, aesSecretKey);
            result = cipher.doFinal(byteArray);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public byte[] getAesDecryptedByteArray(byte[] byteArray) {
        byte[] result = new byte[0];

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, aesSecretKey);
            result = cipher.doFinal(byteArray);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public byte[] getHmacSha1SignedByteArray(byte[] byteArray) {
        byte[] result = new byte[0];
        try {
            Mac mac = Mac.getInstance("HmacSHA1", "BC");
            mac.init(hmacSecretKeySpec);
            result = mac.doFinal(byteArray);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return result;
    }

    public byte[] getEncodedAesSecretKey() {
        return aesSecretKey.getEncoded();
    }

}

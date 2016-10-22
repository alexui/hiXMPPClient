package cs.pub.ro.hixmppclient.otr;

import android.util.Base64;
import android.util.Log;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cs.pub.ro.hixmppclient.general.Constants;

public class OTRUtils {

    static final String alphaNumCharacters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public static String getRandomString(int maxLength) {
        StringBuilder stringBuilder = new StringBuilder(maxLength);
        for (int i = 0; i < maxLength; i++)
            stringBuilder.append(alphaNumCharacters.charAt(rnd.nextInt(alphaNumCharacters.length())));
        return stringBuilder.toString();
    }

    public static KeyPair getRSAKeyPair() {

        KeyPair keyPair = null;
        try {
            SecureRandom secureRandom = new SecureRandom();
            Security.addProvider(new BouncyCastleProvider());
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");

            keyPairGenerator.initialize(Constants.KEY_LENGTH, secureRandom);
            keyPair = keyPairGenerator.generateKeyPair();

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            Log.d(Constants.LOG_TAG, e.getMessage());
            Log.e(Constants.LOG_TAG, e.toString());
        }

        return keyPair;
    }

    public static byte[] getRsaEncryptedByteArray(byte[] byteArray, PrivateKey privateKey) {
        byte[] result = new byte[0];

        try {
            Cipher cipher = Cipher.getInstance("RSA", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] truncatedByteArray = Arrays.copyOfRange(byteArray, 0, Constants.MAX_RSA_LENGTH);
            result = cipher.doFinal(truncatedByteArray);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException | NoSuchPaddingException e) {
            Log.d(Constants.LOG_TAG, e.getMessage());
            Log.e(Constants.LOG_TAG, e.toString());
        }

        return result;
    }

    public static byte[] getAesEncryptedByteArray(byte[] byteArray, SecretKey secretKey) {
        byte[] result = new byte[0];

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            result = cipher.doFinal(byteArray);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException | NoSuchPaddingException e) {
            Log.d(Constants.LOG_TAG, e.getMessage());
            Log.e(Constants.LOG_TAG, e.toString());
        }

        return result;
    }

    public static byte[] getRsaDecryptedByteArray(byte[] byteArray, PublicKey publicKey) {
        byte[] result = new byte[0];

        try {
            Cipher cipher = Cipher.getInstance("RSA", "BC");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            result = cipher.doFinal(byteArray);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException | NoSuchPaddingException e) {
            Log.d(Constants.LOG_TAG, e.getMessage());
            Log.e(Constants.LOG_TAG, e.toString());
        }

        return result;
    }

    public static byte[] getAesDecryptedByteArray(byte[] byteArray, SecretKey secretKey) {
        byte[] result = new byte[0];

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            result = cipher.doFinal(byteArray);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException | NoSuchPaddingException e) {
            Log.d(Constants.LOG_TAG, e.getMessage());
            Log.e(Constants.LOG_TAG, e.toString());
        }

        return result;
    }

    public static PublicKey extractFromEncodedPublicKey(String base64EncodedPublicKey) {

        PublicKey publicKey = null;
        try {
            byte[] encodedPublicKey = Base64.decode(base64EncodedPublicKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(encodedPublicKey);
            publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.d(Constants.LOG_TAG, e.getMessage());
            Log.e(Constants.LOG_TAG, e.toString());
        }

        return publicKey;
    }

    public static PrivateKey loadPrivateKey(String key64) throws GeneralSecurityException {
        byte[] result = Base64.decode(key64, Base64.DEFAULT);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(result);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Arrays.fill(result, (byte) 0);
        return privateKey;
    }


    public static PublicKey loadPublicKey(String key64) throws GeneralSecurityException {
        byte[] result = Base64.decode(key64, Base64.DEFAULT);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(result);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    public static String savePrivateKey(PrivateKey privateKey) throws GeneralSecurityException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = keyFactory.getKeySpec(privateKey, PKCS8EncodedKeySpec.class);
        byte[] result = pkcs8EncodedKeySpec.getEncoded();
        String key64 = Base64.encodeToString(result, Base64.DEFAULT);
        Arrays.fill(result, (byte) 0);
        return key64;
    }


    public static String savePublicKey(PublicKey publicKey) throws GeneralSecurityException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = keyFactory.getKeySpec(publicKey, X509EncodedKeySpec.class);
        return Base64.encodeToString(spec.getEncoded(), Base64.DEFAULT);
    }

    public static String saveSecretKey(SecretKey secretKey) {
        byte encoded[] = secretKey.getEncoded();
        String encodedKey = Base64.encodeToString(encoded, Base64.DEFAULT);
        return encodedKey;
    }

    public static SecretKey loadSecretKey(String secretKeyString) {
        byte[] decodedKey = Base64.decode(secretKeyString, Base64.DEFAULT);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return secretKey;
    }

}

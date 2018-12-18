package de.rememberly.rememberlyandroidapp.apputils;

import android.annotation.TargetApi;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Created by nilsk on 14.11.2018.
 */


public class CryptoManager {
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static SecretKey generateKey(String alias) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeyException {


            KeyGenerator keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);


        final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build();


        keyGenerator.init(keyGenParameterSpec);
        return keyGenerator.generateKey();
    }

    /**
     * Encrypts a String using an alias and the textToEncrypt as String.
     * @param alias The alias is used to define the encrypted String and is used to decrypt.
     * @param textToEncrypt The plaintext String
     * @return Returns a String array containing the used IV and the encryptedText
     */
    public static String[] encryptString(String alias, String textToEncrypt) {
        String[] result = new String[2];
        try {
            SecretKey secretKey = generateKey(alias);
            byte[] iv;
            byte[] encryption;
            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            iv = cipher.getIV();
            encryption = cipher.doFinal(textToEncrypt.getBytes("UTF-8"));
            result[0] = byteArrayToString(iv);
            result[1] = byteArrayToString(encryption);
        } catch (Exception ex) {
            Log.e("Encrypting Exception: ", ex.getMessage());
        }
        return result;
    }
    public static String decryptString (String alias, String IV, String encryptedString) throws
            KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException,
            UnrecoverableEntryException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException
    {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore
                .getEntry(alias, null);

        final SecretKey secretKey = secretKeyEntry.getSecretKey();
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        final GCMParameterSpec spec = new GCMParameterSpec(128, StringToByteArray(IV));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        final byte[] decodedData = cipher.doFinal(StringToByteArray(encryptedString));
        return new String(decodedData, "UTF-8");
    }
    private static String byteArrayToString(byte[] byteArray) {
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }
    private static byte[] StringToByteArray(String string) {
        return Base64.decode(string, Base64.NO_WRAP);
    }
}

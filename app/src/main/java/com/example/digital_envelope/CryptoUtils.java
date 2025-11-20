package com.example.digital_envelope;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.security.KeyStore;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "RSA_KEY_ALIAS";

    // Generate RSA keypair (SHA-1 OAEP, compatible with API 24)
    public static void ensureRSAKeyExists() throws Exception {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEYSTORE);
        ks.load(null);

        if (ks.containsAlias(KEY_ALIAS)) {
            ks.deleteEntry(KEY_ALIAS); // delete old key if exists
        }

        KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setDigests(KeyProperties.DIGEST_SHA1) // SHA-1 for API 24
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                .build();

        KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE);
        kpg.initialize(spec);
        kpg.generateKeyPair();
    }

    // Get public key for AES wrapping
    public static PublicKey getPublicKey() throws Exception {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEYSTORE);
        ks.load(null);
        return ks.getCertificate(KEY_ALIAS).getPublicKey();
    }

    // Wrap AES key with RSA
    public static byte[] wrapAESKey(SecretKey key) throws Exception {
        PublicKey publicKey = getPublicKey();
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding"); // SHA-1 for API 24
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(key.getEncoded());
    }

    // Unwrap AES key with RSA
    public static SecretKey unwrapAESKey(byte[] wrappedKey) throws Exception {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEYSTORE);
        ks.load(null);

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding"); // SHA-1 for API 24
        cipher.init(Cipher.DECRYPT_MODE, ks.getKey(KEY_ALIAS, null));

        byte[] aesBytes = cipher.doFinal(wrappedKey);
        return new SecretKeySpec(aesBytes, "AES");
    }

    // Generate random AES key
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        return kg.generateKey();
    }

    // AES/GCM encryption
    public static EnvelopeFile encryptData(byte[] input) throws Exception {
        SecretKey aesKey = generateAESKey();

        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] iv = aesCipher.getIV();
        byte[] encryptedData = aesCipher.doFinal(input);

        byte[] wrappedKey = wrapAESKey(aesKey);

        return new EnvelopeFile(wrappedKey, iv, encryptedData);
    }

    // AES/GCM decryption
    public static byte[] decryptData(EnvelopeFile env) throws Exception {
        SecretKey aesKey = unwrapAESKey(env.wrappedKey);

        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(128, env.iv));

        return aesCipher.doFinal(env.encryptedData);
    }
}
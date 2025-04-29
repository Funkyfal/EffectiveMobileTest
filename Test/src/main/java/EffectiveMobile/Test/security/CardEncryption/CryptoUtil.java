package EffectiveMobile.Test.security.CardEncryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.util.Base64;

public class CryptoUtil {
    private static final String ALGO = "AES/GCM/NoPadding";
    private static final byte[] IV = new byte[12];

    // Заглушка на данном этапе, в проде должен храниться в безопасном месте
    private static final SecretKey KEY = generateKey();

    private static SecretKey generateKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256);
            return kg.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(String plain) {
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            GCMParameterSpec spec = new GCMParameterSpec(128, IV);
            cipher.init(Cipher.ENCRYPT_MODE, KEY, spec);
            byte[] encrypted = cipher.doFinal(plain.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String encrypted) {
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            GCMParameterSpec spec = new GCMParameterSpec(128, IV);
            cipher.init(Cipher.DECRYPT_MODE, KEY, spec);
            byte[] decoded = Base64.getDecoder().decode(encrypted);
            return new String(cipher.doFinal(decoded));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


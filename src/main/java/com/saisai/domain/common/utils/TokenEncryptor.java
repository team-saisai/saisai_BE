package com.saisai.domain.common.utils;

import static com.saisai.domain.common.exception.ExceptionCode.TOKEN_DECRYPTION_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.TOKEN_ENCRYPTION_FAILED;

import com.saisai.domain.common.exception.CustomException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private final Key key;

    public TokenEncryptor(@Value("${encrypt.key}") String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        this.key = new SecretKeySpec(decodedKey, ALGORITHM);
    }

    public String encrypt(String token) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] ivBytes = new byte[16];
            random.nextBytes(ivBytes);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

            byte[] encrypted = cipher.doFinal(token.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[ivBytes.length + encrypted.length];
            System.arraycopy(ivBytes, 0, combined, 0, ivBytes.length);
            System.arraycopy(encrypted, 0, combined, ivBytes.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);

        } catch (Exception e) {
            throw new CustomException(TOKEN_ENCRYPTION_FAILED, e);
        }
    }

    public String decrypt(String encryptedToken) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedToken);

            byte[] ivBytes = new byte[16];
            byte[] encryptedBytes = new byte[combined.length - 16];
            System.arraycopy(combined, 0, ivBytes, 0, 16);
            System.arraycopy(combined, 16, encryptedBytes, 0, combined.length - 16);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            byte[] decrypted = cipher.doFinal(encryptedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new CustomException(TOKEN_DECRYPTION_FAILED);
        }
    }

}

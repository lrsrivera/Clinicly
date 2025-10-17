package com.icc.clinic.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    
    @Value("${clinic.encryption.key:}")
    private String encryptionKey;
    
    private SecretKey secretKey;

    public void initialize() {
        if (encryptionKey != null && !encryptionKey.trim().isEmpty()) {
            try {
                // Derive a 256-bit key from the string using SHA-256 (must match migration tool)
                java.security.MessageDigest sha = java.security.MessageDigest.getInstance("SHA-256");
                byte[] key = sha.digest(encryptionKey.getBytes(StandardCharsets.UTF_8));
                secretKey = new SecretKeySpec(key, ALGORITHM);
                System.out.println("[ENCRYPTION] Using provided encryption key (SHA-256 derived)");
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize encryption with provided key", e);
            }
        } else {
            // Generate a random key (for development) - note: will not decrypt pre-encrypted data
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
                keyGenerator.init(256, new SecureRandom());
                secretKey = keyGenerator.generateKey();
                System.out.println("[ENCRYPTION] Generated random key for development");
                System.out.println("[ENCRYPTION] WARNING: This key will change on each restart!");
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize encryption", e);
            }
        }
    }

    public String encrypt(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            return plainText;
        }
        
        System.out.println("[EncryptionService] Attempting to encrypt: " + plainText);
        
        try {
            if (secretKey == null) {
                initialize();
            }
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            String result = Base64.getEncoder().encodeToString(encryptedBytes);
            System.out.println("[EncryptionService] Encryption successful: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("[EncryptionService] Encryption failed: " + e.getMessage());
            return plainText; // Return original text if encryption fails
        }
    }

    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.trim().isEmpty()) {
            return encryptedText;
        }
        
        System.out.println("[EncryptionService] Attempting to decrypt: " + encryptedText);
        
        try {
            if (secretKey == null) {
                initialize();
            }
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String result = new String(decryptedBytes, StandardCharsets.UTF_8);
            System.out.println("[EncryptionService] Decryption successful: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("[EncryptionService] Decryption failed: " + e.getMessage());
            System.err.println("[EncryptionService] Returning original text: " + encryptedText);
            return encryptedText; // Return original text if decryption fails
        }
    }

    public boolean isEncrypted(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        try {
            Base64.getDecoder().decode(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

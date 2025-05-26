package com.example.game.utils;

import android.os.Build;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;
import android.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SenhaUtils {


    // Número de interações que o hash faz com o código
    private static final int ITERATIONS = 10000;

    // 256bits usado
    private static final int KEY_LENGTH = 256;

    // Algoritmo usado: PBKDF2
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    // Codifica array de bytes para Base64
    private static String encodeToBase64(byte[] input) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return java.util.Base64.getEncoder().encodeToString(input);
        } else {
            return Base64.encodeToString(input, Base64.NO_WRAP);
        }
    }

    // Gera um salt (dificulta o acesso da senha) aleatório
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return encodeToBase64(salt);
    }

    // Gera o hash (mistura senha normal + o salt)
    public static String hashPassword(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return encodeToBase64(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }

    // Gera senha segura com salt
    public static String generateSecurePassword(String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        return salt + ":" + hashedPassword;
    }

    // Verifica se a senha digitada corresponde à armazenada
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        String[] parts = storedPassword.split(":");
        if (parts.length != 2) {
            return false;
        }
        String salt = parts[0];
        String hashOfInput = hashPassword(inputPassword, salt);
        return hashOfInput.equals(parts[1]);
    }
}


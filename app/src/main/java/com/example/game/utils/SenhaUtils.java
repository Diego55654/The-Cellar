package com.example.game.utils;

import android.os.Build;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SenhaUtils {

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    // Gera um salt aleatório
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(salt);
        }
        return "";
    }

    // Gera o hash da senha usando PBKDF2
    public static String hashPassword(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(hash);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }

        return password;
    }

    // Método para criar um hash de senha junto com o salt
    public static String generateSecurePassword(String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        return salt + ":" + hashedPassword;  // Armazena salt e hash juntos
    }

    // Método para verificar a senha digitada
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        String[] parts = storedPassword.split(":");
        if (parts.length != 2) {
            return false;
        }
        String salt = parts[0];
        String hashOfInput = hashPassword(inputPassword, salt);
        return hashOfInput.equals(parts[1]);  // Compara hash armazenado com hash da entrada
    }
}

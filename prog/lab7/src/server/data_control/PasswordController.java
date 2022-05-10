package server.data_control;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordController {
    public static boolean checkPasswords(String password, String salt, String hashPassword) {
        return createHash(password + salt).equals(hashPassword);
    }

    public static String createHash(String password) {
        try {
            byte[] bytes = MessageDigest.getInstance("sha-256").digest(password.getBytes());
            return new BigInteger(bytes).toString();
        } catch (NoSuchAlgorithmException e) {
            return password;
        }

    }

    public static String generateSalt() {
        byte[] salt = new byte[6];
        new Random().nextBytes(salt);
        return new BigInteger(salt).toString();
    }
}

package cncs.academy.ess.PBKDF2;

import cncs.academy.ess.model.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

// Implementing PBKDF2 in Java
public class SecurePassword {
    public static void main(String[] args) throws Exception {
        String password = "mySecurePassword";
//        byte[] salt = generateSalt(); // Generate a random salt
//        int iterations = 10000;
//        int keyLength = 256;
//        // Hash the password using PBKDF2
//        byte[] hashedPassword = hashPassword(password, salt, iterations, keyLength);
//
//        // Convert the hashed password to a string for storage
//        String hashedPasswordString = bytesToHex(hashedPassword);
//        String saltString = bytesToHex(salt);
//        String saltWithPasswd = saltString + " : " + hashedPasswordString;
//        System.out.println("Hashed Password: " + hashedPasswordString);
    }
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            bytes[i] = (byte) Integer.parseInt(hex.substring(index, index + 2), 16);
        }
        return bytes;
    }

    public static byte[] hashPassword(String password, byte[] salt, int iterations, int keyLength) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16 bytes for the salt
        random.nextBytes(salt);
        return salt;
    }

    public static String hashNewPasswd( String password) throws Exception {
        byte[] salt = generateSalt();
        int iterations = 10000;
        int keyLength = 256;
        // Hash the password using PBKDF2
        byte[] hashedPassword = hashPassword(password, salt, iterations, keyLength);
        String hashedPasswordString = bytesToHex(hashedPassword);
        String saltString = bytesToHex(salt);
        return saltString + " : " + hashedPasswordString;
    }

    public static boolean hashLoginPasswd( String password, String passwordDB) throws Exception {
        String[] parts = passwordDB.split(" : ");
        byte[] salt = hexToBytes(parts[0]);
        byte[] hashPassword = hexToBytes(parts[1]);
        int iterations = 10000;
        int keyLength = 256;
        // Hash the password using PBKDF2
        byte[] hashedPassword = hashPassword(password, salt, iterations, keyLength);
        String hashedPasswordString = bytesToHex(hashedPassword);
        String saltString = bytesToHex(salt);
        String LoginPasswd = saltString + " : " + hashedPasswordString;
        if (LoginPasswd.equals(passwordDB)) {
            return true;
        }
        else {
        return false;
        }
    }
}

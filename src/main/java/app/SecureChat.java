package app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecureChat {
    //Funksioni per me i hashing masazhet
    public static String hashMessage(String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(message.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                hexString.append(Integer.toHexString(0xff & b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    //Ruajta e mesazhit në bazën e të dhënave
    public static void storeMessage(String sender, String recipient, String message) {
        String url = "jdbc:mysql://localhost:3306/secure_chat";
        String user = "root";
        String password = "root1234";

        String messageHash = hashMessage(message);

        String sql = "INSERT INTO messages (sender, recipient, message_hash) VALUES (?, ?, ?)";
    }

}

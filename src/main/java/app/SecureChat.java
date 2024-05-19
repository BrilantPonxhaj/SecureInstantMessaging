package app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sender);
            pstmt.setString(2, recipient);
            pstmt.setString(3, messageHash);

            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecureChat {
  //Funksioni per me i hashing masazhet
    public static String hashMessage(String message) {
        try{
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

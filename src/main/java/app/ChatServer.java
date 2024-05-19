import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class ChatServer {
}
 class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private SecretKeySpec aesKey;

    public ClientHandler(Socket socket) throws Exception {
        this.socket = socket;
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());

        // Generate server DH key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(2048);
        KeyPair serverKeyPair = keyPairGenerator.generateKeyPair();

        // Send public key to client
        byte[] serverPublicKeyEnc = serverKeyPair.getPublic().getEncoded();
        output.writeInt(serverPublicKeyEnc.length);
        output.write(serverPublicKeyEnc);

        // Receive client's public key
        int clientKeyLength = input.readInt();
        byte[] clientPublicKeyEnc = new byte[clientKeyLength];
        input.readFully(clientPublicKeyEnc);

        // Generate shared secret
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(clientPublicKeyEnc);
        PublicKey clientPublicKey = keyFactory.generatePublic(x509KeySpec);

        KeyAgreement keyAgree = KeyAgreement.getInstance("DH");
        keyAgree.init(serverKeyPair.getPrivate());
        keyAgree.doPhase(clientPublicKey, true);

        byte[] sharedSecret = keyAgree.generateSecret();

        // Derive a key from the shared secret
        MessageDigest hash = MessageDigest.getInstance("SHA-256");
        byte[] derivedKey = hash.digest(sharedSecret);

        System.out.println("Shared secret established with client");

        // Use the derived key for AES encryption
        aesKey = new SecretKeySpec(derivedKey, 0, 16, "AES");
    }

     @Override
     public void run() {
         try {
             while (true) {
                 int length = input.readInt();
                 if (length > 0) {
                     byte[] encryptedMessage = new byte[length];
                     input.readFully(encryptedMessage, 0, encryptedMessage.length);
                     byte[] iv = new byte[12];
                     input.readFully(iv);

                     Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                     GCMParameterSpec spec = new GCMParameterSpec(128, iv);
                     cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);
                     byte[] decryptedMessage = cipher.doFinal(encryptedMessage);

                     String message = new String(decryptedMessage);
                     System.out.println("Received: " + message);

                     // Broadcast message to all clients
                     synchronized (clients) {
                         for (ClientHandler client : clients) {
                             if (client != this) {
                                 client.sendMessage(message);
                             }
                         }
                     }
                 }
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

     private void sendMessage(String message) throws Exception {
         byte[] iv = new byte[12];
         SecureRandom random = new SecureRandom();
         random.nextBytes(iv);

         Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
         GCMParameterSpec spec = new GCMParameterSpec(128, iv);
         cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
         byte[] encryptedMessage = cipher.doFinal(message.getBytes());

         output.writeInt(encryptedMessage.length);
         output.write(encryptedMessage);
         output.write(iv);
     }
 }



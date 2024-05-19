package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SIMPGUI extends Application {
    private static final String HOST = "";
    private static final int PORT = 6868;

    private TextArea messageArea;
    private TextField inputField;
    private DataOutputStream output;
    private SecretKeySpec aesKey;
    private String username;
    @Override
    public void start(Stage stage){
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setWrapText(true);

        inputField = new TextField();
        inputField.setPromptText("Enter your message...");
        inputField.setOnAction(e -> sendMessage());

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        HBox hbox = new HBox(10, inputField, sendButton);
        hbox.setHgrow(inputField, Priority.ALWAYS);

        vbox.getChildren().addAll(new Label("Messages:"), new ScrollPane(messageArea), hbox);

        Scene scene = new Scene(vbox, 400, 300);
        stage.setScene(scene);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Username");
        dialog.setHeaderText("Enter your username:");
        dialog.setContentText("Username:");

        dialog.showAndWait().ifPresent(name -> {
            username = name;
            stage.setTitle("Secure Chat Client - " + username);
            stage.show();

            // Connect to server and start communication
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> connectToServer());
        });
    }
    public static void main(String[] args) {
        launch(args);
    }

    private void connectToServer() {
        try {
            System.out.println("Attempting to connect to server at " + HOST + ":" + PORT);
            Socket socket = new Socket(HOST, PORT);
            System.out.println("Connected to server");

            DataInputStream input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            // Generate client DH key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(2048);
            KeyPair clientKeyPair = keyPairGenerator.generateKeyPair();

            // Receive server's public key
            int serverKeyLength = input.readInt();
            byte[] serverPublicKeyEnc = new byte[serverKeyLength];
            input.readFully(serverPublicKeyEnc);

            // Send public key to server
            byte[] clientPublicKeyEnc = clientKeyPair.getPublic().getEncoded();
            output.writeInt(clientPublicKeyEnc.length);
            output.write(clientPublicKeyEnc);

            // Generate shared secret
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPublicKeyEnc);
            PublicKey serverPublicKey = keyFactory.generatePublic(x509KeySpec);

            KeyAgreement keyAgree = KeyAgreement.getInstance("DH");
            keyAgree.init(clientKeyPair.getPrivate());
            keyAgree.doPhase(serverPublicKey, true);

            byte[] sharedSecret = keyAgree.generateSecret();

            // Derive a key from the shared secret
            MessageDigest hash = MessageDigest.getInstance("SHA-256");
            byte[] derivedKey = hash.digest(sharedSecret);

            System.out.println("Shared secret established");

            // Use the derived key for AES encryption
            aesKey = new SecretKeySpec(derivedKey, 0, 16, "AES");

            // Verify key initialization
            System.out.println("AES Key: " + Arrays.toString(aesKey.getEncoded()));

            // Start a thread to listen for messages
            new Thread(new MessageReceiver(input, aesKey, messageArea, username)).start();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            Platform.runLater(() -> showAlert("Connection Error", "Unable to connect to the server. Please make sure the server is running and try again."));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        try {
            String message = inputField.getText();
            if (message.isEmpty()) {
                return;
            }

            String fullMessage = username + ": " + message;

            byte[] iv = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
            byte[] encryptedMessage = cipher.doFinal(fullMessage.getBytes());

            output.writeInt(encryptedMessage.length);
            output.write(encryptedMessage);
            output.write(iv);

            Platform.runLater(() -> {
                messageArea.appendText(fullMessage + "\n");
                inputField.clear();
            });

            // Save the message to the database
            SecureChat.storeMessage(username, "Broadcast", fullMessage);
        } catch (InvalidKeyException e) {
            System.err.println("InvalidKeyException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


class MessageReceiver implements Runnable {
        private DataInputStream input;
        private SecretKeySpec aesKey;
        private TextArea messageArea;
        private String username;

        public MessageReceiver(DataInputStream input, SecretKeySpec aesKey, TextArea messageArea, String username) {
            this.input = input;
            this.aesKey = aesKey;
            this.messageArea = messageArea;
            this.username = username;
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
                        Platform.runLater(() -> messageArea.appendText(message + "\n"));

                        // Save the received message to the database
                        String[] messageParts = message.split(": ", 2);
                        if (messageParts.length == 2) {
                            String sender = messageParts[0];
                            String content = messageParts[1];
                            SecureChat.storeMessage(sender, username, content);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
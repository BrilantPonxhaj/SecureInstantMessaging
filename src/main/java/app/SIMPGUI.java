package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.crypto.spec.SecretKeySpec;
import java.io.DataOutputStream;

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

        Button sendButton = new Button("Send");

        HBox hbox = new HBox(10, inputField, sendButton);
        hbox.setHgrow(inputField, Priority.ALWAYS);

        vbox.getChildren().addAll(new Label("Messages:"), new ScrollPane(messageArea), hbox);

        Scene scene = new Scene(vbox, 400, 300);
        stage.setScene(scene);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Username");
        dialog.setHeaderText("Enter your username:");
        dialog.setContentText("Username:");
    }
    public static void main(String[] args) {
        launch(args);
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
}

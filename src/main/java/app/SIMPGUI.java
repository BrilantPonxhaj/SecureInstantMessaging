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

}

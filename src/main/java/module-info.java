module com.example.datasecurity4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.datasecurity4 to javafx.fxml;
    exports app;
    opens app to javafx.fxml;
}
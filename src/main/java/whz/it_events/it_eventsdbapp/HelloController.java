package whz.it_events.it_eventsdbapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import whz.it_events.it_eventsdbapp.config.DbConnection;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        DbConnection connection = new DbConnection();
        connection.getConnection();
        connection.close();
    }
}

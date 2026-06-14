package whz.it_events.it_eventsdbapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import whz.it_events.it_eventsdbapp.config.JpaUtil;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/whz/it_events/it_eventsdbapp/main-view.fxml")
        );
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource("/whz/it_events/it_eventsdbapp/styles.css").toExternalForm()
        );

        stage.setTitle("IT Events - Verwaltung");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        JpaUtil.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

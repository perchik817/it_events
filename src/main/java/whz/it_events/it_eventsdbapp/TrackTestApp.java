package whz.it_events.it_eventsdbapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import whz.it_events.it_eventsdbapp.config.JpaUtil;

import java.io.IOException;

/**
 * Temporary test app: opens only the Track tab in its own window,
 * so it can be tested independently of the rest of the GUI.
 */
public class TrackTestApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/whz/it_events/it_eventsdbapp/track-view.fxml")
        );
        Parent root = loader.load();

        stage.setTitle("Track - Test");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void stop() {
        // close JPA resources cleanly when the window is closed
        JpaUtil.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

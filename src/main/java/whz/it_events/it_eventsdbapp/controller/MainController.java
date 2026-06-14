package whz.it_events.it_eventsdbapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.io.IOException;

public class MainController {

    @FXML private Tab eventTab;
    @FXML private Tab trackTab;
    @FXML private Tab teamTab;
    @FXML private Tab sessionTab;
    @FXML private Tab submissionTab;
    @FXML private Tab scoreTab;
    @FXML private Tab preisTab;
    @FXML private Tab sponsorTab;
    @FXML private Tab locationTab;
    @FXML private Tab speakerTab;
    @FXML private Tab userTab;

    @FXML
    public void initialize() {
        loadInto(eventTab,      "/whz/it_events/it_eventsdbapp/event-view.fxml");
        loadInto(trackTab,      "/whz/it_events/it_eventsdbapp/track-view.fxml");
        loadInto(teamTab,       "/whz/it_events/it_eventsdbapp/team-view.fxml");
        loadInto(sessionTab,    "/whz/it_events/it_eventsdbapp/session-view.fxml");
        loadInto(submissionTab, "/whz/it_events/it_eventsdbapp/submission-view.fxml");
        loadInto(scoreTab,      "/whz/it_events/it_eventsdbapp/score-view.fxml");
        loadInto(preisTab,      "/whz/it_events/it_eventsdbapp/preis-view.fxml");
        loadInto(sponsorTab,    "/whz/it_events/it_eventsdbapp/sponsor-view.fxml");
        loadInto(locationTab,   "/whz/it_events/it_eventsdbapp/location-view.fxml");
        loadInto(speakerTab,    "/whz/it_events/it_eventsdbapp/speaker-view.fxml");
        loadInto(userTab,       "/whz/it_events/it_eventsdbapp/user-view.fxml");
    }

    private void loadInto(Tab tab, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            tab.setContent(content);
        } catch (IOException e) {
            throw new RuntimeException("Could not load tab content: " + fxmlPath, e);
        }
    }
}

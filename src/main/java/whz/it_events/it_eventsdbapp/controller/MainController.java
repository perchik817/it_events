package whz.it_events.it_eventsdbapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import whz.it_events.it_eventsdbapp.SessionContext;
import whz.it_events.it_eventsdbapp.model.enums.Role;

import java.io.IOException;

public class MainController {

    @FXML private TabPane mainTabPane;

    @FXML private Tab eventTab;
    @FXML private Tab trackTab;
    @FXML private Tab teamTab;
    @FXML private Tab sessionTab;
    @FXML private Tab submissionTab;
    @FXML private Tab scoreTab;
    @FXML private Tab preisTab;
    @FXML private Tab participantTab;
    @FXML private Tab memberTab;
    @FXML private Tab juryTab;
    @FXML private Tab mentorTab;
    @FXML private Tab organisatorTab;
    @FXML private Tab eventSponsorTab;
    @FXML private Tab visitorTab;
    @FXML private Tab preisSponsorTab;
    @FXML private Tab sponsorTab;
    @FXML private Tab locationTab;
    @FXML private Tab speakerTab;
    @FXML private Tab userTab;

    @FXML
    public void initialize() {
        loadInto(eventTab,         "/whz/it_events/it_eventsdbapp/event-view.fxml");
        loadInto(trackTab,         "/whz/it_events/it_eventsdbapp/track-view.fxml");
        loadInto(teamTab,          "/whz/it_events/it_eventsdbapp/team-view.fxml");
        loadInto(sessionTab,       "/whz/it_events/it_eventsdbapp/session-view.fxml");
        loadInto(submissionTab,    "/whz/it_events/it_eventsdbapp/submission-view.fxml");
        loadInto(scoreTab,         "/whz/it_events/it_eventsdbapp/score-view.fxml");
        loadInto(preisTab,         "/whz/it_events/it_eventsdbapp/preis-view.fxml");
        loadInto(participantTab,   "/whz/it_events/it_eventsdbapp/participant-view.fxml");
        loadInto(memberTab,        "/whz/it_events/it_eventsdbapp/member-view.fxml");
        loadInto(juryTab,          "/whz/it_events/it_eventsdbapp/jury-view.fxml");
        loadInto(mentorTab,        "/whz/it_events/it_eventsdbapp/mentor-view.fxml");
        loadInto(organisatorTab,   "/whz/it_events/it_eventsdbapp/organisator-view.fxml");
        loadInto(eventSponsorTab,  "/whz/it_events/it_eventsdbapp/event-sponsor-view.fxml");
        loadInto(visitorTab,       "/whz/it_events/it_eventsdbapp/visitor-view.fxml");
        loadInto(preisSponsorTab,  "/whz/it_events/it_eventsdbapp/preis-sponsor-view.fxml");
        loadInto(sponsorTab,       "/whz/it_events/it_eventsdbapp/sponsor-view.fxml");
        loadInto(locationTab,      "/whz/it_events/it_eventsdbapp/location-view.fxml");
        loadInto(speakerTab,       "/whz/it_events/it_eventsdbapp/speaker-view.fxml");
        loadInto(userTab,          "/whz/it_events/it_eventsdbapp/user-view.fxml");

        applyRoleVisibility();
    }

    private void applyRoleVisibility() {
        Role role = SessionContext.getRole();

        if (role == Role.USER) {
            // USER sieht nicht: Participants, Visitors, Scores, Users
            mainTabPane.getTabs().removeAll(
                    participantTab, visitorTab, scoreTab, userTab
            );
        } else if (role == Role.JURY) {
            // JURY sieht nicht: Participants, Visitors, Users
            mainTabPane.getTabs().removeAll(
                    participantTab, visitorTab, userTab
            );
        }
        // ADMIN sieht alles
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

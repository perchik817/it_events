package whz.it_events.it_eventsdbapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import whz.it_events.it_eventsdbapp.SessionContext;
import whz.it_events.it_eventsdbapp.model.User;
import whz.it_events.it_eventsdbapp.service.XmlExportService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminController {

    @FXML private Label adminNameLabel;
    @FXML private Label adminEmailLabel;
    @FXML private BorderPane contentPane;

    @FXML private Button btnEvents;
    @FXML private Button btnTracks;
    @FXML private Button btnSessions;
    @FXML private Button btnLocations;
    @FXML private Button btnUsers;
    @FXML private Button btnTeams;
    @FXML private Button btnMembers;
    @FXML private Button btnParticipants;
    @FXML private Button btnSubmissions;
    @FXML private Button btnScores;
    @FXML private Button btnPreise;
    @FXML private Button btnJury;
    @FXML private Button btnSponsors;
    @FXML private Button btnEventSponsors;
    @FXML private Button btnPreisSponsors;
    @FXML private Button btnSpeakers;
    @FXML private Button btnMentors;
    @FXML private Button btnOrganisators;
    @FXML private Button btnVisitors;
    @FXML private Button btnXmlExport;

    private static final String ACTIVE =
            "-fx-background-color: #5B6EF5; -fx-text-fill: white; -fx-background-radius: 10; " +
            "-fx-padding: 10 14; -fx-alignment: CENTER-LEFT; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String INACTIVE =
            "-fx-background-color: transparent; -fx-text-fill: #8A8FA3; -fx-background-radius: 10; " +
            "-fx-padding: 10 14; -fx-alignment: CENTER-LEFT; -fx-cursor: hand;";

    private Button currentActive;

    @FXML
    public void initialize() {
        User user = SessionContext.getCurrentUser();
        adminNameLabel.setText(user.getName() + " " + user.getLastname());
        adminEmailLabel.setText(user.getEmail());
        selectEvents();
    }

    @FXML public void selectEvents()       { load("event-view.fxml", btnEvents); }
    @FXML public void selectTracks()       { load("track-view.fxml", btnTracks); }
    @FXML public void selectSessions()     { load("session-view.fxml", btnSessions); }
    @FXML public void selectLocations()    { load("location-view.fxml", btnLocations); }
    @FXML public void selectUsers()        { load("user-view.fxml", btnUsers); }
    @FXML public void selectTeams()        { load("team-view.fxml", btnTeams); }
    @FXML public void selectMembers()      { load("member-view.fxml", btnMembers); }
    @FXML public void selectParticipants() { load("participant-view.fxml", btnParticipants); }
    @FXML public void selectSubmissions()  { load("submission-view.fxml", btnSubmissions); }
    @FXML public void selectScores()       { load("score-view.fxml", btnScores); }
    @FXML public void selectPreise()       { load("preis-view.fxml", btnPreise); }
    @FXML public void selectJury()         { load("jury-view.fxml", btnJury); }
    @FXML public void selectSponsors()     { load("sponsor-view.fxml", btnSponsors); }
    @FXML public void selectEventSponsors(){ load("event-sponsor-view.fxml", btnEventSponsors); }
    @FXML public void selectPreisSponsors(){ load("preis-sponsor-view.fxml", btnPreisSponsors); }
    @FXML public void selectSpeakers()     { load("speaker-view.fxml", btnSpeakers); }
    @FXML public void selectMentors()      { load("mentor-view.fxml", btnMentors); }
    @FXML public void selectOrganisators() { load("organisator-view.fxml", btnOrganisators); }
    @FXML public void selectVisitors()     { load("visitor-view.fxml", btnVisitors); }

    private void load(String fxml, Button active) {
        try {
            Parent content = FXMLLoader.load(
                    getClass().getResource("/whz/it_events/it_eventsdbapp/" + fxml)
            );
            contentPane.setCenter(content);
            if (currentActive != null) currentActive.setStyle(INACTIVE);
            active.setStyle(ACTIVE);
            currentActive = active;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onXmlExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("XML Export speichern");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML-Dateien (*.xml)", "*.xml")
        );
        String defaultName = "it_events_export_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xml";
        fileChooser.setInitialFileName(defaultName);

        Stage stage = (Stage) adminNameLabel.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return; // Benutzer hat abgebrochen
        }

        try {
            new XmlExportService().exportToFile(file);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export erfolgreich");
            alert.setHeaderText(null);
            alert.setContentText("Die Daten wurden erfolgreich exportiert nach:\n" + file.getAbsolutePath());
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export fehlgeschlagen");
            alert.setHeaderText(null);
            alert.setContentText("Beim Export ist ein Fehler aufgetreten:\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void onLogout() {
        try {
            SessionContext.clear();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/whz/it_events/it_eventsdbapp/login-view.fxml")
            );
            Parent root = loader.load();
            Stage stage = (Stage) adminNameLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/whz/it_events/it_eventsdbapp/styles.css").toExternalForm()
            );
            stage.setTitle("IT Events - Anmeldung");
            stage.setMaximized(false);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.scene.layout.VBox;
import whz.it_events.it_eventsdbapp.SessionContext;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.TeamRepository;
import whz.it_events.it_eventsdbapp.dao.TrackRepository;
import whz.it_events.it_eventsdbapp.model.Team;
import whz.it_events.it_eventsdbapp.model.Track;

import java.time.LocalDateTime;

public class TeamController {

    @FXML private TableView<Team> teamTable;
    @FXML private TableColumn<Team, Long> colId;
    @FXML private TableColumn<Team, String> colName;
    @FXML private TableColumn<Team, String> colTrack;
    @FXML private TableColumn<Team, Integer> colScore;
    @FXML private TableColumn<Team, String> colRegDate;

    @FXML private TextField nameField;
    @FXML private ComboBox<Track> trackComboBox;
    @FXML private VBox rightPanel;
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private EntityManager entityManager;
    private TeamRepository teamRepository;
    private TrackRepository trackRepository;

    private final ObservableList<Team> teamData = FXCollections.observableArrayList();
    private Team currentTeam;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        teamRepository = new TeamRepository(entityManager, Team.class);
        trackRepository = new TrackRepository(entityManager, Track.class);

        setupTable();
        setupTrackComboBox();
        loadTeams();

        teamTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showTeamInForm(newVal)
        );

        onNew();
        applyRoleAccess();
    }

    private void setupTable() {
        colId.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId()));
        colName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        colScore.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getScoreValue()));
        colTrack.setCellValueFactory(cellData -> {
            Track track = cellData.getValue().getTrack();
            return new SimpleStringProperty(track != null ? track.getName() : "");
        });
        colRegDate.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getRegistrationDate();
            return new SimpleStringProperty(date != null ? date.toLocalDate().toString() : "");
        });
        teamTable.setItems(teamData);
    }

    private void setupTrackComboBox() {
        trackComboBox.setItems(FXCollections.observableArrayList(trackRepository.findAll()));
        trackComboBox.setConverter(new StringConverter<Track>() {
            @Override public String toString(Track t) { return t != null ? t.getName() : ""; }
            @Override public Track fromString(String s) {
                return trackComboBox.getItems().stream()
                        .filter(t -> t.getName().equals(s)).findFirst().orElse(null);
            }
        });
    }

    private void loadTeams() {
        teamData.setAll(teamRepository.findAll());
    }

    private void showTeamInForm(Team team) {
        currentTeam = team;
        if (team == null) { clearForm(); return; }
        nameField.setText(team.getName());
        trackComboBox.setValue(team.getTrack());
        statusLabel.setText("");
    }

    private void clearForm() {
        nameField.clear();
        trackComboBox.setValue(null);
        statusLabel.setText("");
    }

    @FXML private void onNew() {
        currentTeam = null;
        teamTable.getSelectionModel().clearSelection();
        clearForm();
    }

    @FXML private void onSave() {
        String name = nameField.getText();
        if (name == null || name.isBlank()) {
            statusLabel.setText("Name darf nicht leer sein.");
            return;
        }
        Track track = trackComboBox.getValue();
        if (track == null) {
            statusLabel.setText("Bitte einen Track auswählen.");
            return;
        }

        boolean isNew = (currentTeam == null);
        Team team = isNew ? new Team(name, track) : currentTeam;
        if (!isNew) {
            team.setName(name);
            team.setTrack(track);
        }

        try {
            teamRepository.save(team);
            statusLabel.setText("Gespeichert.");
            loadTeams();
            onNew();
        applyRoleAccess();
        } catch (Exception e) {
            statusLabel.setText("Fehler: " + e.getMessage());
        }
    }

    @FXML private void onDelete() {
        if (currentTeam == null || currentTeam.getId() == null) {
            statusLabel.setText("Bitte zuerst ein Team auswählen.");
            return;
        }
        try {
            teamRepository.delete(currentTeam);
            statusLabel.setText("Gelöscht.");
            loadTeams();
            onNew();
        applyRoleAccess();
        } catch (Exception e) {
            statusLabel.setText("Fehler: " + e.getMessage());
        }
    }

    private void applyRoleAccess() {
        boolean isAdmin = SessionContext.isAdmin();
        // Only ADMIN sees the right form panel
        if (rightPanel != null) {
            rightPanel.setVisible(isAdmin);
            rightPanel.setManaged(isAdmin);
        }
        newButton.setDisable(!isAdmin);
        saveButton.setDisable(!isAdmin);
        deleteButton.setDisable(!isAdmin);
    }
}

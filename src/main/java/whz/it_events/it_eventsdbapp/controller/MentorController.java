package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.scene.layout.VBox;
import whz.it_events.it_eventsdbapp.SessionContext;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.MentorRepository;
import whz.it_events.it_eventsdbapp.dao.TrackRepository;
import whz.it_events.it_eventsdbapp.dao.UserRepository;
import whz.it_events.it_eventsdbapp.model.*;

public class MentorController {
    @FXML private TableView<Mentor> mentorTable;
    @FXML private TableColumn<Mentor, Long> colId;
    @FXML private TableColumn<Mentor, String> colUser;
    @FXML private TableColumn<Mentor, String> colTrack;
    @FXML private TableColumn<Mentor, String> colProfArea;
    @FXML private ComboBox<User> userComboBox;
    @FXML private ComboBox<Track> trackComboBox;
    @FXML private TextField profAreaField;
    @FXML private VBox rightPanel;
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private EntityManager em;
    private MentorRepository mentorRepo;
    private UserRepository userRepo;
    private TrackRepository trackRepo;
    private final ObservableList<Mentor> data = FXCollections.observableArrayList();
    private Mentor current;

    @FXML public void initialize() {
        em = JpaUtil.getEntityManager();
        mentorRepo = new MentorRepository(em, Mentor.class);
        userRepo = new UserRepository(em, User.class);
        trackRepo = new TrackRepository(em, Track.class);

        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colUser.setCellValueFactory(c -> { User u = c.getValue().getUser(); return new SimpleStringProperty(u != null ? u.getName() + " " + u.getLastname() : ""); });
        colTrack.setCellValueFactory(c -> { Track t = c.getValue().getTrack(); return new SimpleStringProperty(t != null ? t.getName() : ""); });
        colProfArea.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProfArea()));
        mentorTable.setItems(data);

        userComboBox.setItems(FXCollections.observableArrayList(userRepo.findAll()));
        userComboBox.setConverter(new StringConverter<User>() {
            @Override public String toString(User u) { return u != null ? u.getName() + " " + u.getLastname() : ""; }
            @Override public User fromString(String s) { return null; }
        });
        trackComboBox.setItems(FXCollections.observableArrayList(trackRepo.findAll()));
        trackComboBox.setConverter(new StringConverter<Track>() {
            @Override public String toString(Track t) { return t != null ? t.getName() : ""; }
            @Override public Track fromString(String s) { return null; }
        });

        mentorTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> showInForm(n));
        onNew();
        applyRoleAccess(); load();
    }

    private void load() { data.setAll(mentorRepo.findAll()); }
    private void showInForm(Mentor m) { current = m; if (m == null) { clearForm(); return; } userComboBox.setValue(m.getUser()); trackComboBox.setValue(m.getTrack()); profAreaField.setText(m.getProfArea()); statusLabel.setText(""); }
    private void clearForm() { userComboBox.setValue(null); trackComboBox.setValue(null); profAreaField.clear(); statusLabel.setText(""); }

    @FXML private void onNew() { current = null; mentorTable.getSelectionModel().clearSelection(); clearForm(); }
    @FXML private void onSave() {
        User user = userComboBox.getValue();
        if (user == null) { statusLabel.setText("Bitte einen User auswählen."); return; }
        Mentor m = (current != null) ? current : new Mentor(user, trackComboBox.getValue(), profAreaField.getText());
        if (current != null) { m.setUser(user); m.setTrack(trackComboBox.getValue()); m.setProfArea(profAreaField.getText()); }
        try { mentorRepo.save(m); statusLabel.setText("Gespeichert."); load(); onNew();
        applyRoleAccess(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }
    @FXML private void onDelete() {
        if (current == null) { statusLabel.setText("Bitte zuerst auswählen."); return; }
        try { mentorRepo.delete(current); statusLabel.setText("Gelöscht."); load(); onNew();
        applyRoleAccess(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
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

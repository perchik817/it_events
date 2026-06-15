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
import whz.it_events.it_eventsdbapp.dao.JuryRepository;
import whz.it_events.it_eventsdbapp.dao.TrackRepository;
import whz.it_events.it_eventsdbapp.dao.UserRepository;
import whz.it_events.it_eventsdbapp.model.Jury;
import whz.it_events.it_eventsdbapp.model.Track;
import whz.it_events.it_eventsdbapp.model.User;

public class JuryController {

    @FXML private TableView<Jury> juryTable;
    @FXML private TableColumn<Jury, Long> colId;
    @FXML private TableColumn<Jury, String> colUser;
    @FXML private TableColumn<Jury, String> colTrack;
    @FXML private TableColumn<Jury, String> colProfArea;
    @FXML private TableColumn<Jury, String> colInfo;

    @FXML private ComboBox<User> userComboBox;
    @FXML private ComboBox<Track> trackComboBox;
    @FXML private TextField profAreaField;
    @FXML private TextField infoField;
    @FXML private VBox rightPanel;
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private EntityManager entityManager;
    private JuryRepository juryRepository;
    private UserRepository userRepository;
    private TrackRepository trackRepository;

    private final ObservableList<Jury> data = FXCollections.observableArrayList();
    private Jury current;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        juryRepository = new JuryRepository(entityManager, Jury.class);
        userRepository = new UserRepository(entityManager, User.class);
        trackRepository = new TrackRepository(entityManager, Track.class);

        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colUser.setCellValueFactory(c -> {
            User u = c.getValue().getUser();
            return new SimpleStringProperty(u != null ? u.getName() + " " + u.getLastname() : "");
        });
        colTrack.setCellValueFactory(c -> {
            Track t = c.getValue().getTrack();
            return new SimpleStringProperty(t != null ? t.getName() : "");
        });
        colProfArea.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProfArea()));
        colInfo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInfo()));

        juryTable.setItems(data);

        userComboBox.setItems(FXCollections.observableArrayList(userRepository.findAll()));
        userComboBox.setConverter(new StringConverter<User>() {
            @Override public String toString(User u) { return u != null ? u.getName() + " " + u.getLastname() : ""; }
            @Override public User fromString(String s) { return null; }
        });

        trackComboBox.setItems(FXCollections.observableArrayList(trackRepository.findAll()));
        trackComboBox.setConverter(new StringConverter<Track>() {
            @Override public String toString(Track t) { return t != null ? t.getName() : ""; }
            @Override public Track fromString(String s) { return null; }
        });

        juryTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> showInForm(n));
        onNew();
        applyRoleAccess();
        load();
    }

    private void load() { data.setAll(juryRepository.findAll()); }

    private void showInForm(Jury j) {
        current = j;
        if (j == null) { clearForm(); return; }
        userComboBox.setValue(j.getUser());
        trackComboBox.setValue(j.getTrack());
        profAreaField.setText(j.getProfArea());
        infoField.setText(j.getInfo());
        statusLabel.setText("");
    }

    private void clearForm() {
        userComboBox.setValue(null);
        trackComboBox.setValue(null);
        profAreaField.clear();
        infoField.clear();
        statusLabel.setText("");
    }

    @FXML private void onNew() {
        current = null;
        juryTable.getSelectionModel().clearSelection();
        clearForm();
    }

    @FXML private void onSave() {
        User user = userComboBox.getValue();
        Track track = trackComboBox.getValue();
        if (user == null) { statusLabel.setText("Bitte einen User auswählen."); return; }

        Jury j = (current != null) ? current
                : new Jury(user, track, profAreaField.getText(), infoField.getText());
        if (current != null) {
            j.setUser(user);
            j.setTrack(track);
            j.setProfArea(profAreaField.getText());
            j.setInfo(infoField.getText());
        }

        try { juryRepository.save(j); statusLabel.setText("Gespeichert."); load(); onNew();
        applyRoleAccess(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }

    @FXML private void onDelete() {
        if (current == null) { statusLabel.setText("Bitte zuerst auswählen."); return; }
        try { juryRepository.delete(current); statusLabel.setText("Gelöscht."); load(); onNew();
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

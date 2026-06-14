package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.ParticipantRepository;
import whz.it_events.it_eventsdbapp.dao.SubmissionRepository;
import whz.it_events.it_eventsdbapp.dao.TrackRepository;
import whz.it_events.it_eventsdbapp.dao.UserRepository;
import whz.it_events.it_eventsdbapp.model.Participant;
import whz.it_events.it_eventsdbapp.model.Submission;
import whz.it_events.it_eventsdbapp.model.Track;
import whz.it_events.it_eventsdbapp.model.User;

public class ParticipantController {

    @FXML private TableView<Participant> participantTable;
    @FXML private TableColumn<Participant, Long> colId;
    @FXML private TableColumn<Participant, String> colUser;
    @FXML private TableColumn<Participant, String> colTrack;
    @FXML private TableColumn<Participant, String> colSubmission;

    @FXML private ComboBox<User> userComboBox;
    @FXML private ComboBox<Track> trackComboBox;
    @FXML private ComboBox<Submission> submissionComboBox;
    @FXML private Label statusLabel;

    private EntityManager entityManager;
    private ParticipantRepository participantRepository;
    private UserRepository userRepository;
    private TrackRepository trackRepository;
    private SubmissionRepository submissionRepository;

    private final ObservableList<Participant> data = FXCollections.observableArrayList();
    private Participant current;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        participantRepository = new ParticipantRepository(entityManager, Participant.class);
        userRepository = new UserRepository(entityManager, User.class);
        trackRepository = new TrackRepository(entityManager, Track.class);
        submissionRepository = new SubmissionRepository(entityManager, Submission.class);

        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colUser.setCellValueFactory(c -> {
            User u = c.getValue().getUser();
            return new SimpleStringProperty(u != null ? u.getName() + " " + u.getLastname() : "");
        });
        colTrack.setCellValueFactory(c -> {
            Track t = c.getValue().getTrack();
            return new SimpleStringProperty(t != null ? t.getName() : "");
        });
        colSubmission.setCellValueFactory(c -> {
            Submission s = c.getValue().getSubmission();
            return new SimpleStringProperty(s != null ? s.getTitel() : "");
        });

        participantTable.setItems(data);

        setupComboBox(userComboBox, userRepository.findAll(),
                u -> u.getName() + " " + u.getLastname());
        setupComboBox(trackComboBox, trackRepository.findAll(), Track::getName);
        setupComboBox(submissionComboBox, submissionRepository.findAll(), Submission::getTitel);

        participantTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> showInForm(n));
        onNew();
        load();
    }

    private <T> void setupComboBox(ComboBox<T> box, java.util.List<T> items,
                                    java.util.function.Function<T, String> nameFunc) {
        box.setItems(FXCollections.observableArrayList(items));
        box.setConverter(new StringConverter<T>() {
            @Override public String toString(T t) { return t != null ? nameFunc.apply(t) : ""; }
            @Override public T fromString(String s) {
                return box.getItems().stream()
                        .filter(i -> nameFunc.apply(i).equals(s)).findFirst().orElse(null);
            }
        });
    }

    private void load() { data.setAll(participantRepository.findAll()); }

    private void showInForm(Participant p) {
        current = p;
        if (p == null) { clearForm(); return; }
        userComboBox.setValue(p.getUser());
        trackComboBox.setValue(p.getTrack());
        submissionComboBox.setValue(p.getSubmission());
        statusLabel.setText("");
    }

    private void clearForm() {
        userComboBox.setValue(null);
        trackComboBox.setValue(null);
        submissionComboBox.setValue(null);
        statusLabel.setText("");
    }

    @FXML private void onNew() {
        current = null;
        participantTable.getSelectionModel().clearSelection();
        clearForm();
    }

    @FXML private void onSave() {
        User user = userComboBox.getValue();
        Track track = trackComboBox.getValue();
        if (user == null || track == null) {
            statusLabel.setText("User und Track sind pflichtfelder.");
            return;
        }
        Participant p = (current != null) ? current
                : new Participant(user, track, submissionComboBox.getValue());
        if (current != null) {
            p.setUser(user);
            p.setTrack(track);
            p.setSubmission(submissionComboBox.getValue());
        }
        try { participantRepository.save(p); statusLabel.setText("Gespeichert."); load(); onNew(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }

    @FXML private void onDelete() {
        if (current == null) { statusLabel.setText("Bitte zuerst auswählen."); return; }
        try { participantRepository.delete(current); statusLabel.setText("Gelöscht."); load(); onNew(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }
}

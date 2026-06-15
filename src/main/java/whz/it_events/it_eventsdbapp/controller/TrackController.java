package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import javafx.scene.layout.VBox;
import whz.it_events.it_eventsdbapp.SessionContext;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.EventRepository;
import whz.it_events.it_eventsdbapp.dao.TrackRepository;
import whz.it_events.it_eventsdbapp.model.Event;
import whz.it_events.it_eventsdbapp.model.Track;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TrackController {

    @FXML private TableView<Track> trackTable;
    @FXML private TableColumn<Track, Long> colId;
    @FXML private TableColumn<Track, String> colName;
    @FXML private TableColumn<Track, String> colEvent;
    @FXML private TableColumn<Track, String> colDeadline;
    @FXML private TableColumn<Track, String> colDescription;

    @FXML private TextField nameField;
    @FXML private ComboBox<Event> eventComboBox;
    @FXML private DatePicker deadlineDatePicker;
    @FXML private TextArea descriptionArea;
    @FXML private VBox rightPanel;
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private EntityManager entityManager;
    private TrackRepository trackRepository;
    private EventRepository eventRepository;

    private final ObservableList<Track> trackData = FXCollections.observableArrayList();

    // currently selected/edited track; null = "Neu" (creating a new one)
    private Track currentTrack;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        trackRepository = new TrackRepository(entityManager, Track.class);
        eventRepository = new EventRepository(entityManager, Event.class);

        setupTable();
        setupEventComboBox();
        loadTracks();

        trackTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showTrackInForm(newVal)
        );

        onNew();
        applyRoleAccess();
    }

    private void setupTable() {
        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));
        colName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        colDescription.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));

        colEvent.setCellValueFactory(cellData -> {
            Event event = cellData.getValue().getEvent();
            String name = (event != null) ? event.getName() : "";
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        colDeadline.setCellValueFactory(cellData -> {
            LocalDateTime deadline = cellData.getValue().getDeadlineDate();
            String text = (deadline != null) ? deadline.toLocalDate().toString() : "";
            return new javafx.beans.property.SimpleStringProperty(text);
        });

        trackTable.setItems(trackData);
    }

    private void setupEventComboBox() {
        ObservableList<Event> events = FXCollections.observableArrayList(eventRepository.findAll());
        eventComboBox.setItems(events);

        // show event name instead of Event@hashcode
        eventComboBox.setConverter(new StringConverter<Event>() {
            @Override
            public String toString(Event event) {
                return (event != null) ? event.getName() : "";
            }

            @Override
            public Event fromString(String string) {
                return eventComboBox.getItems().stream()
                        .filter(e -> e.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    private void loadTracks() {
        trackData.setAll(trackRepository.findAll());
    }

    /** Fills the form with the data of the selected track. */
    private void showTrackInForm(Track track) {
        currentTrack = track;
        if (track == null) {
            clearForm();
            return;
        }
        nameField.setText(track.getName());
        descriptionArea.setText(track.getDescription());
        eventComboBox.setValue(track.getEvent());

        LocalDateTime deadline = track.getDeadlineDate();
        deadlineDatePicker.setValue(deadline != null ? deadline.toLocalDate() : null);

        statusLabel.setText("");
    }

    private void clearForm() {
        nameField.clear();
        descriptionArea.clear();
        eventComboBox.setValue(null);
        deadlineDatePicker.setValue(null);
        statusLabel.setText("");
    }

    /** "Neu" button: clears the form so a new Track can be created. */
    @FXML
    private void onNew() {
        currentTrack = null;
        trackTable.getSelectionModel().clearSelection();
        clearForm();
    }

    /** "Speichern" button: creates a new Track or updates the selected one. */
    @FXML
    private void onSave() {
        String name = nameField.getText();
        if (name == null || name.isBlank()) {
            statusLabel.setText("Name darf nicht leer sein.");
            return;
        }

        Event selectedEvent = eventComboBox.getValue();
        if (selectedEvent == null) {
            statusLabel.setText("Bitte ein Event auswählen.");
            return;
        }

        LocalDate date = deadlineDatePicker.getValue();
        LocalDateTime deadline = (date != null) ? date.atStartOfDay() : null;

        Track track = (currentTrack != null) ? currentTrack : new Track();
        track.setName(name);
        track.setDescription(descriptionArea.getText());
        track.setEvent(selectedEvent);
        track.setDeadlineDate(deadline);

        try {
            trackRepository.save(track);
            statusLabel.setText("Gespeichert.");
            loadTracks();
            onNew();
        applyRoleAccess();
        } catch (Exception e) {
            statusLabel.setText("Fehler beim Speichern: " + e.getMessage());
        }
    }

    /** "Löschen" button: deletes the selected Track. */
    @FXML
    private void onDelete() {
        if (currentTrack == null || currentTrack.getId() == null) {
            statusLabel.setText("Bitte zuerst einen Track auswählen.");
            return;
        }

        try {
            trackRepository.delete(currentTrack);
            statusLabel.setText("Gelöscht.");
            loadTracks();
            onNew();
        applyRoleAccess();
        } catch (Exception e) {
            statusLabel.setText("Fehler beim Löschen: " + e.getMessage());
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

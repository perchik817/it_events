package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.SpeakerRepository;
import whz.it_events.it_eventsdbapp.model.Speaker;

public class SpeakerController {

    @FXML private TableView<Speaker> speakerTable;
    @FXML private TableColumn<Speaker, Long> colId;
    @FXML private TableColumn<Speaker, String> colName;
    @FXML private TableColumn<Speaker, String> colContact;

    @FXML private TextField nameField;
    @FXML private TextField contactField;
    @FXML private Label statusLabel;

    private EntityManager entityManager;
    private SpeakerRepository speakerRepository;

    private final ObservableList<Speaker> speakerData = FXCollections.observableArrayList();

    // currently selected/edited speaker; null = "Neu" (creating a new one)
    private Speaker currentSpeaker;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        speakerRepository = new SpeakerRepository(entityManager, Speaker.class);

        setupTable();
        loadSpeakers();

        speakerTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showSpeakerInForm(newVal)
        );

        onNew();
    }

    private void setupTable() {
        colId.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId()));
        colName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        colContact.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getContact()));

        speakerTable.setItems(speakerData);
    }

    private void loadSpeakers() {
        speakerData.setAll(speakerRepository.findAll());
    }

    /** Fills the form with the data of the selected speaker. */
    private void showSpeakerInForm(Speaker speaker) {
        currentSpeaker = speaker;
        if (speaker == null) {
            clearForm();
            return;
        }
        nameField.setText(speaker.getName());
        contactField.setText(speaker.getContact());
        statusLabel.setText("");
    }

    private void clearForm() {
        nameField.clear();
        contactField.clear();
        statusLabel.setText("");
    }

    /** "Neu" button: clears the form so a new Speaker can be created. */
    @FXML
    private void onNew() {
        currentSpeaker = null;
        speakerTable.getSelectionModel().clearSelection();
        clearForm();
    }

    /** "Speichern" button: creates a new Speaker or updates the selected one. */
    @FXML
    private void onSave() {
        String name = nameField.getText();
        if (name == null || name.isBlank()) {
            statusLabel.setText("Name darf nicht leer sein.");
            return;
        }

        Speaker speaker = (currentSpeaker != null) ? currentSpeaker : new Speaker();
        speaker.setName(name);
        speaker.setContact(contactField.getText());

        try {
            speakerRepository.save(speaker);
            statusLabel.setText("Gespeichert.");
            loadSpeakers();
            onNew();
        } catch (Exception e) {
            statusLabel.setText("Fehler beim Speichern: " + e.getMessage());
        }
    }

    /** "Löschen" button: deletes the selected Speaker. */
    @FXML
    private void onDelete() {
        if (currentSpeaker == null || currentSpeaker.getId() == null) {
            statusLabel.setText("Bitte zuerst einen Speaker auswählen.");
            return;
        }

        try {
            speakerRepository.delete(currentSpeaker);
            statusLabel.setText("Gelöscht.");
            loadSpeakers();
            onNew();
        } catch (Exception e) {
            statusLabel.setText("Fehler beim Löschen: " + e.getMessage());
        }
    }
}

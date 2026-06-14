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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.SubmissionRepository;
import whz.it_events.it_eventsdbapp.model.Submission;
import whz.it_events.it_eventsdbapp.model.enums.ParticipationType;
import whz.it_events.it_eventsdbapp.model.enums.SubmissionStatus;

import java.time.LocalDateTime;

public class SubmissionController {

    @FXML private TableView<Submission> submissionTable;
    @FXML private TableColumn<Submission, Long> colId;
    @FXML private TableColumn<Submission, String> colTitel;
    @FXML private TableColumn<Submission, String> colStatus;
    @FXML private TableColumn<Submission, String> colType;
    @FXML private TableColumn<Submission, String> colTime;
    @FXML private TableColumn<Submission, String> colComment;

    @FXML private TextField titelField;
    @FXML private TextArea commentArea;
    @FXML private ComboBox<SubmissionStatus> statusComboBox;
    @FXML private ComboBox<ParticipationType> typeComboBox;
    @FXML private Label statusLabel;

    private EntityManager entityManager;
    private SubmissionRepository submissionRepository;
    private final ObservableList<Submission> data = FXCollections.observableArrayList();
    private Submission current;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        submissionRepository = new SubmissionRepository(entityManager, Submission.class);

        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colTitel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitel()));
        colComment.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getComment()));
        colType.setCellValueFactory(c -> {
            ParticipationType t = c.getValue().getParticipationType();
            return new SimpleStringProperty(t != null ? t.toString() : "");
        });
        colStatus.setCellValueFactory(c -> {
            SubmissionStatus s = c.getValue().getStatus();
            return new SimpleStringProperty(s != null ? s.toString() : "");
        });
        colTime.setCellValueFactory(c -> {
            LocalDateTime t = c.getValue().getSubmissionTime();
            return new SimpleStringProperty(t != null ? t.toLocalDate().toString() : "");
        });

        submissionTable.setItems(data);
        statusComboBox.setItems(FXCollections.observableArrayList(SubmissionStatus.values()));
        typeComboBox.setItems(FXCollections.observableArrayList(ParticipationType.values()));

        submissionTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> showInForm(n));
        onNew();
        load();
    }

    private void load() { data.setAll(submissionRepository.findAll()); }

    private void showInForm(Submission s) {
        current = s;
        if (s == null) { clearForm(); return; }
        titelField.setText(s.getTitel());
        commentArea.setText(s.getComment());
        statusComboBox.setValue(s.getStatus());
        typeComboBox.setValue(s.getParticipationType());
        statusLabel.setText("");
    }

    private void clearForm() {
        titelField.clear();
        commentArea.clear();
        statusComboBox.setValue(null);
        typeComboBox.setValue(null);
        statusLabel.setText("");
    }

    @FXML private void onNew() {
        current = null;
        submissionTable.getSelectionModel().clearSelection();
        clearForm();
    }

    @FXML private void onSave() {
        String titel = titelField.getText();
        if (titel == null || titel.isBlank()) {
            statusLabel.setText("Titel darf nicht leer sein.");
            return;
        }
        ParticipationType type = typeComboBox.getValue();
        if (type == null) {
            statusLabel.setText("Bitte Teilnahme-Typ auswählen.");
            return;
        }

        boolean isNew = (current == null);
        Submission s = isNew ? new Submission(titel, commentArea.getText(), type) : current;
        if (!isNew) {
            s.setTitel(titel);
            s.setComment(commentArea.getText());
            s.setParticipationType(type);
        }
        s.setStatus(statusComboBox.getValue());

        try { submissionRepository.save(s); statusLabel.setText("Gespeichert."); load(); onNew(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }

    @FXML private void onDelete() {
        if (current == null) { statusLabel.setText("Bitte zuerst auswählen."); return; }
        try { submissionRepository.delete(current); statusLabel.setText("Gelöscht."); load(); onNew(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }
}

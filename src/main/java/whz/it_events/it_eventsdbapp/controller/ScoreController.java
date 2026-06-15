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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import whz.it_events.it_eventsdbapp.SessionContext;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.JuryRepository;
import whz.it_events.it_eventsdbapp.dao.ScoreRepository;
import whz.it_events.it_eventsdbapp.dao.SubmissionRepository;
import whz.it_events.it_eventsdbapp.model.Jury;
import whz.it_events.it_eventsdbapp.model.Score;
import whz.it_events.it_eventsdbapp.model.Submission;

import java.time.LocalDateTime;

public class ScoreController {

    @FXML private TableView<Score> scoreTable;
    @FXML private TableColumn<Score, Long> colId;
    @FXML private TableColumn<Score, String> colSubmission;
    @FXML private TableColumn<Score, String> colJury;
    @FXML private TableColumn<Score, String> colCriteria;
    @FXML private TableColumn<Score, Integer> colValue;
    @FXML private TableColumn<Score, String> colComment;
    @FXML private TableColumn<Score, String> colDate;

    @FXML private ComboBox<Submission> submissionComboBox;
    @FXML private ComboBox<Jury> juryComboBox;
    @FXML private TextField criteriaField;
    @FXML private TextField valueField;
    @FXML private TextArea commentArea;
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private EntityManager entityManager;
    private ScoreRepository scoreRepository;
    private SubmissionRepository submissionRepository;
    private JuryRepository juryRepository;

    private final ObservableList<Score> data = FXCollections.observableArrayList();
    private Score current;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        scoreRepository = new ScoreRepository(entityManager, Score.class);
        submissionRepository = new SubmissionRepository(entityManager, Submission.class);
        juryRepository = new JuryRepository(entityManager, Jury.class);

        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colCriteria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCriteria()));
        colComment.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getComment()));
        colValue.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getScoreValue()));
        colSubmission.setCellValueFactory(c -> {
            Submission s = c.getValue().getSubmission();
            return new SimpleStringProperty(s != null ? s.getTitel() : "");
        });
        colJury.setCellValueFactory(c -> {
            Jury j = c.getValue().getJury();
            if (j == null) return new SimpleStringProperty("");
            return new SimpleStringProperty(j.getUser() != null ?
                    j.getUser().getName() + " " + j.getUser().getLastname() : "Jury #" + j.getId());
        });
        colDate.setCellValueFactory(c -> {
            LocalDateTime d = c.getValue().getReviewDate();
            return new SimpleStringProperty(d != null ? d.toLocalDate().toString() : "");
        });

        scoreTable.setItems(data);

        submissionComboBox.setItems(FXCollections.observableArrayList(submissionRepository.findAll()));
        submissionComboBox.setConverter(new StringConverter<Submission>() {
            @Override public String toString(Submission s) { return s != null ? s.getTitel() : ""; }
            @Override public Submission fromString(String str) { return null; }
        });

        juryComboBox.setItems(FXCollections.observableArrayList(juryRepository.findAll()));
        juryComboBox.setConverter(new StringConverter<Jury>() {
            @Override public String toString(Jury j) {
                if (j == null) return "";
                return j.getUser() != null ?
                        j.getUser().getName() + " " + j.getUser().getLastname() : "Jury #" + j.getId();
            }
            @Override public Jury fromString(String str) { return null; }
        });

        scoreTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> showInForm(n));
        onNew();
        applyRoleAccess();
        load();
    }

    private void load() { data.setAll(scoreRepository.findAll()); }

    private void showInForm(Score s) {
        current = s;
        if (s == null) { clearForm(); return; }
        submissionComboBox.setValue(s.getSubmission());
        juryComboBox.setValue(s.getJury());
        criteriaField.setText(s.getCriteria());
        valueField.setText(s.getScoreValue() != null ? s.getScoreValue().toString() : "");
        commentArea.setText(s.getComment());
        statusLabel.setText("");
    }

    private void clearForm() {
        submissionComboBox.setValue(null);
        juryComboBox.setValue(null);
        criteriaField.clear();
        valueField.clear();
        commentArea.clear();
        statusLabel.setText("");
    }

    @FXML private void onNew() {
        current = null;
        scoreTable.getSelectionModel().clearSelection();
        clearForm();
    }

    @FXML private void onSave() {
        Submission sub = submissionComboBox.getValue();
        if (sub == null) { statusLabel.setText("Bitte eine Submission auswählen."); return; }
        String criteria = criteriaField.getText();
        if (criteria == null || criteria.isBlank()) { statusLabel.setText("Kriterien darf nicht leer sein."); return; }
        int value;
        try { value = Integer.parseInt(valueField.getText().trim()); }
        catch (NumberFormatException e) { statusLabel.setText("Wert muss eine Zahl (0-10) sein."); return; }

        boolean isNew = (current == null);
        Score score = isNew
                ? new Score(criteria, value, commentArea.getText(), LocalDateTime.now(), sub, juryComboBox.getValue())
                : current;
        if (!isNew) {
            score.setSubmission(sub);
            score.setJury(juryComboBox.getValue());
            score.setCriteria(criteria);
            score.setScoreValue(value);
            score.setComment(commentArea.getText());
        }

        try { scoreRepository.save(score); statusLabel.setText("Gespeichert."); load(); onNew();
        applyRoleAccess(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }

    @FXML private void onDelete() {
        if (current == null) { statusLabel.setText("Bitte zuerst auswählen."); return; }
        try { scoreRepository.delete(current); statusLabel.setText("Gelöscht."); load(); onNew();
        applyRoleAccess(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }

    private void applyRoleAccess() {
        // JURY can write scores, ADMIN cannot, USER cannot
        boolean canWrite = SessionContext.isJury();
        newButton.setDisable(!canWrite);
        saveButton.setDisable(!canWrite);
        deleteButton.setDisable(!canWrite);
    }
}

package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.scene.layout.VBox;
import whz.it_events.it_eventsdbapp.SessionContext;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.EventRepository;
import whz.it_events.it_eventsdbapp.dao.SessionRepository;
import whz.it_events.it_eventsdbapp.model.Event;
import whz.it_events.it_eventsdbapp.model.Session;
import whz.it_events.it_eventsdbapp.model.enums.SessionType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SessionController {

    @FXML private TableView<Session> sessionTable;
    @FXML private TableColumn<Session, Long> colId;
    @FXML private TableColumn<Session, String> colTitel;
    @FXML private TableColumn<Session, String> colEvent;
    @FXML private TableColumn<Session, String> colType;
    @FXML private TableColumn<Session, String> colRoom;
    @FXML private TableColumn<Session, Integer> colCapacity;
    @FXML private TableColumn<Session, String> colStartDate;
    @FXML private TableColumn<Session, String> colEndDate;

    @FXML private TextField titelField;
    @FXML private ComboBox<Event> eventComboBox;
    @FXML private ComboBox<SessionType> typeComboBox;
    @FXML private TextField roomField;
    @FXML private TextField capacityField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private VBox rightPanel;
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private EntityManager entityManager;
    private SessionRepository sessionRepository;
    private EventRepository eventRepository;

    private final ObservableList<Session> sessionData = FXCollections.observableArrayList();
    private Session currentSession;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        sessionRepository = new SessionRepository(entityManager, Session.class);
        eventRepository = new EventRepository(entityManager, Event.class);

        setupTable();
        setupEventComboBox();
        setupTypeComboBox();
        loadSessions();

        sessionTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showSessionInForm(newVal)
        );

        onNew();
        applyRoleAccess();
    }

    private void setupTable() {
        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colTitel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitel()));
        colRoom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRoom()));
        colCapacity.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCapacity()));
        colEvent.setCellValueFactory(c -> {
            Event e = c.getValue().getEvent();
            return new SimpleStringProperty(e != null ? e.getName() : "");
        });
        colType.setCellValueFactory(c -> {
            SessionType t = c.getValue().getSessionType();
            return new SimpleStringProperty(t != null ? t.toString() : "");
        });
        colStartDate.setCellValueFactory(c -> {
            LocalDateTime d = c.getValue().getStartDate();
            return new SimpleStringProperty(d != null ? d.toLocalDate().toString() : "");
        });
        colEndDate.setCellValueFactory(c -> {
            LocalDateTime d = c.getValue().getEndDate();
            return new SimpleStringProperty(d != null ? d.toLocalDate().toString() : "");
        });
        sessionTable.setItems(sessionData);
    }

    private void setupEventComboBox() {
        eventComboBox.setItems(FXCollections.observableArrayList(eventRepository.findAllOrderedByStartDate()));
        eventComboBox.setConverter(new StringConverter<Event>() {
            @Override public String toString(Event e) { return e != null ? e.getName() : ""; }
            @Override public Event fromString(String s) {
                return eventComboBox.getItems().stream()
                        .filter(e -> e.getName().equals(s)).findFirst().orElse(null);
            }
        });
    }

    private void setupTypeComboBox() {
        typeComboBox.setItems(FXCollections.observableArrayList(SessionType.values()));
    }

    private void loadSessions() {
        sessionData.setAll(sessionRepository.findAll());
    }

    private void showSessionInForm(Session session) {
        currentSession = session;
        if (session == null) { clearForm(); return; }
        titelField.setText(session.getTitel());
        eventComboBox.setValue(session.getEvent());
        typeComboBox.setValue(session.getSessionType());
        roomField.setText(session.getRoom());
        capacityField.setText(String.valueOf(session.getCapacity()));
        LocalDateTime start = session.getStartDate();
        startDatePicker.setValue(start != null ? start.toLocalDate() : null);
        LocalDateTime end = session.getEndDate();
        endDatePicker.setValue(end != null ? end.toLocalDate() : null);
        statusLabel.setText("");
    }

    private void clearForm() {
        titelField.clear();
        eventComboBox.setValue(null);
        typeComboBox.setValue(null);
        roomField.clear();
        capacityField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        statusLabel.setText("");
    }

    @FXML private void onNew() {
        currentSession = null;
        sessionTable.getSelectionModel().clearSelection();
        clearForm();
    }

    @FXML private void onSave() {
        String titel = titelField.getText();
        if (titel == null || titel.isBlank()) {
            statusLabel.setText("Titel darf nicht leer sein.");
            return;
        }
        Event event = eventComboBox.getValue();
        if (event == null) {
            statusLabel.setText("Bitte ein Event auswählen.");
            return;
        }
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        if (start == null || end == null) {
            statusLabel.setText("Bitte Start- und Enddatum auswählen.");
            return;
        }
        int capacity = 0;
        try {
            capacity = Integer.parseInt(capacityField.getText().trim());
        } catch (NumberFormatException e) {
            statusLabel.setText("Kapazität muss eine Zahl sein.");
            return;
        }

        Session session = (currentSession != null) ? currentSession : new Session();
        session.setTitel(titel);
        session.setEvent(event);
        session.setSessionType(typeComboBox.getValue());
        session.setRoom(roomField.getText());
        session.setCapacity(capacity);
        session.setStartDate(start.atStartOfDay());
        session.setEndDate(end.atStartOfDay());
        session.setDescription(null);

        try {
            sessionRepository.save(session);
            statusLabel.setText("Gespeichert.");
            loadSessions();
            onNew();
        applyRoleAccess();
        } catch (Exception e) {
            statusLabel.setText("Fehler: " + e.getMessage());
        }
    }

    @FXML private void onDelete() {
        if (currentSession == null || currentSession.getId() == null) {
            statusLabel.setText("Bitte zuerst eine Session auswählen.");
            return;
        }
        try {
            sessionRepository.delete(currentSession);
            statusLabel.setText("Gelöscht.");
            loadSessions();
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

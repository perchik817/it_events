package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.EventRepository;
import whz.it_events.it_eventsdbapp.dao.LocationRepository;
import whz.it_events.it_eventsdbapp.model.Event;
import whz.it_events.it_eventsdbapp.model.Location;
import whz.it_events.it_eventsdbapp.model.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventController {

    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, Long> colId;
    @FXML private TableColumn<Event, String> colName;
    @FXML private TableColumn<Event, String> colLocation;
    @FXML private TableColumn<Event, String> colStartDate;
    @FXML private TableColumn<Event, String> colEndDate;
    @FXML private TableColumn<Event, String> colStatus;
    @FXML private TableColumn<Event, String> colDescription;

    @FXML private TextField nameField;
    @FXML private ComboBox<Location> locationComboBox;
    @FXML private ComboBox<Status> statusComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextArea descriptionArea;
    @FXML private Label statusLabel;

    private EntityManager entityManager;
    private EventRepository eventRepository;
    private LocationRepository locationRepository;

    private final ObservableList<Event> eventData = FXCollections.observableArrayList();

    // currently selected/edited event; null = "Neu" (creating a new one)
    private Event currentEvent;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        eventRepository = new EventRepository(entityManager, Event.class);
        locationRepository = new LocationRepository(entityManager, Location.class);

        setupTable();
        setupLocationComboBox();
        setupStatusComboBox();
        loadEvents();

        eventTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showEventInForm(newVal)
        );

        onNew();
    }

    private void setupTable() {
        colId.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId()));
        colName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        colDescription.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));

        colLocation.setCellValueFactory(cellData -> {
            Location location = cellData.getValue().getLocation();
            String name = (location != null) ? location.getLocationName() : "";
            return new SimpleStringProperty(name);
        });

        colStartDate.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getStartDate();
            return new SimpleStringProperty(date != null ? date.toLocalDate().toString() : "");
        });

        colEndDate.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getEndDate();
            return new SimpleStringProperty(date != null ? date.toLocalDate().toString() : "");
        });

        colStatus.setCellValueFactory(cellData -> {
            Status status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status != null ? status.toString() : "");
        });

        eventTable.setItems(eventData);
    }

    private void setupLocationComboBox() {
        ObservableList<Location> locations = FXCollections.observableArrayList(locationRepository.findAll());
        locationComboBox.setItems(locations);

        locationComboBox.setConverter(new StringConverter<Location>() {
            @Override
            public String toString(Location location) {
                return (location != null) ? location.getLocationName() : "";
            }

            @Override
            public Location fromString(String string) {
                return locationComboBox.getItems().stream()
                        .filter(l -> l.getLocationName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    private void setupStatusComboBox() {
        statusComboBox.setItems(FXCollections.observableArrayList(Status.values()));
    }

    private void loadEvents() {
        eventData.setAll(eventRepository.findAllOrderedByStartDate());
    }

    /** Fills the form with the data of the selected event. */
    private void showEventInForm(Event event) {
        currentEvent = event;
        if (event == null) {
            clearForm();
            return;
        }
        nameField.setText(event.getName());
        descriptionArea.setText(event.getDescription());
        locationComboBox.setValue(event.getLocation());
        statusComboBox.setValue(event.getStatus());

        LocalDateTime start = event.getStartDate();
        startDatePicker.setValue(start != null ? start.toLocalDate() : null);

        LocalDateTime end = event.getEndDate();
        endDatePicker.setValue(end != null ? end.toLocalDate() : null);

        statusLabel.setText("");
    }

    private void clearForm() {
        nameField.clear();
        descriptionArea.clear();
        locationComboBox.setValue(null);
        statusComboBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        statusLabel.setText("");
    }

    /** "Neu" button: clears the form so a new Event can be created. */
    @FXML
    private void onNew() {
        currentEvent = null;
        eventTable.getSelectionModel().clearSelection();
        clearForm();
    }

    /** "Speichern" button: creates a new Event or updates the selected one. */
    @FXML
    private void onSave() {
        String name = nameField.getText();
        if (name == null || name.isBlank()) {
            statusLabel.setText("Name darf nicht leer sein.");
            return;
        }

        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        if (startDate == null || endDate == null) {
            statusLabel.setText("Bitte Start- und Enddatum auswählen.");
            return;
        }

        Status status = statusComboBox.getValue();
        if (status == null) {
            statusLabel.setText("Bitte einen Status auswählen.");
            return;
        }

        Event event = (currentEvent != null) ? currentEvent : new Event();
        event.setName(name);
        event.setDescription(descriptionArea.getText());
        event.setLocation(locationComboBox.getValue());
        event.setStatus(status);
        event.setStartDate(startDate.atStartOfDay());
        event.setEndDate(endDate.atStartOfDay());

        try {
            eventRepository.save(event);
            statusLabel.setText("Gespeichert.");
            loadEvents();
            onNew();
        } catch (Exception e) {
            statusLabel.setText("Fehler beim Speichern: " + e.getMessage());
        }
    }

    /** "Löschen" button: deletes the selected Event. */
    @FXML
    private void onDelete() {
        if (currentEvent == null || currentEvent.getId() == null) {
            statusLabel.setText("Bitte zuerst ein Event auswählen.");
            return;
        }

        try {
            eventRepository.delete(currentEvent);
            statusLabel.setText("Gelöscht.");
            loadEvents();
            onNew();
        } catch (Exception e) {
            statusLabel.setText("Fehler beim Löschen: " + e.getMessage());
        }
    }
}

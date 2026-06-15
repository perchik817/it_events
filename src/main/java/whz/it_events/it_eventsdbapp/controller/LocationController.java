package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import whz.it_events.it_eventsdbapp.SessionContext;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.LocationRepository;
import whz.it_events.it_eventsdbapp.model.Location;

public class LocationController {

    @FXML private TableView<Location> locationTable;
    @FXML private TableColumn<Location, Long> colId;
    @FXML private TableColumn<Location, String> colLocationName;
    @FXML private TableColumn<Location, String> colStadt;
    @FXML private TableColumn<Location, String> colAddress;

    @FXML private TextField locationNameField;
    @FXML private TextField stadtField;
    @FXML private TextField addressField;
    @FXML private VBox rightPanel;
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private EntityManager entityManager;
    private LocationRepository locationRepository;

    private final ObservableList<Location> locationData = FXCollections.observableArrayList();

    // currently selected/edited location; null = "Neu" (creating a new one)
    private Location currentLocation;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        locationRepository = new LocationRepository(entityManager, Location.class);

        setupTable();
        loadLocations();

        locationTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showLocationInForm(newVal)
        );

        onNew();
        applyRoleAccess();
    }

    private void setupTable() {
        colId.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId()));
        colLocationName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLocationName()));
        colStadt.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStadt()));
        colAddress.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAddress()));

        locationTable.setItems(locationData);
    }

    private void loadLocations() {
        locationData.setAll(locationRepository.findAll());
    }

    /** Fills the form with the data of the selected location. */
    private void showLocationInForm(Location location) {
        currentLocation = location;
        if (location == null) {
            clearForm();
            return;
        }
        locationNameField.setText(location.getLocationName());
        stadtField.setText(location.getStadt());
        addressField.setText(location.getAddress());
        statusLabel.setText("");
    }

    private void clearForm() {
        locationNameField.clear();
        stadtField.clear();
        addressField.clear();
        statusLabel.setText("");
    }

    /** "Neu" button: clears the form so a new Location can be created. */
    @FXML
    private void onNew() {
        currentLocation = null;
        locationTable.getSelectionModel().clearSelection();
        clearForm();
    }

    /** "Speichern" button: creates a new Location or updates the selected one. */
    @FXML
    private void onSave() {
        String name = locationNameField.getText();
        if (name == null || name.isBlank()) {
            statusLabel.setText("Name darf nicht leer sein.");
            return;
        }

        Location location = (currentLocation != null) ? currentLocation : new Location();
        location.setLocationName(name);
        location.setStadt(stadtField.getText());
        location.setAddress(addressField.getText());

        try {
            locationRepository.save(location);
            statusLabel.setText("Gespeichert.");
            loadLocations();
            onNew();
        applyRoleAccess();
        } catch (Exception e) {
            statusLabel.setText("Fehler beim Speichern: " + e.getMessage());
        }
    }

    /** "Löschen" button: deletes the selected Location. */
    @FXML
    private void onDelete() {
        if (currentLocation == null || currentLocation.getId() == null) {
            statusLabel.setText("Bitte zuerst eine Location auswählen.");
            return;
        }

        try {
            locationRepository.delete(currentLocation);
            statusLabel.setText("Gelöscht.");
            loadLocations();
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

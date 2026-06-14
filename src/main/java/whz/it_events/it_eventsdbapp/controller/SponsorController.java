package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.SponsorRepository;
import whz.it_events.it_eventsdbapp.model.Sponsor;

public class SponsorController {

    @FXML private TableView<Sponsor> sponsorTable;
    @FXML private TableColumn<Sponsor, Long> colId;
    @FXML private TableColumn<Sponsor, String> colName;
    @FXML private TableColumn<Sponsor, String> colContact;
    @FXML private TableColumn<Sponsor, String> colPhotoUrl;

    @FXML private TextField nameField;
    @FXML private TextField contactField;
    @FXML private TextField photoUrlField;
    @FXML private Label statusLabel;

    private EntityManager entityManager;
    private SponsorRepository sponsorRepository;

    private final ObservableList<Sponsor> sponsorData = FXCollections.observableArrayList();

    // currently selected/edited sponsor; null = "Neu" (creating a new one)
    private Sponsor currentSponsor;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        sponsorRepository = new SponsorRepository(entityManager, Sponsor.class);

        setupTable();
        loadSponsors();

        sponsorTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showSponsorInForm(newVal)
        );

        onNew();
    }

    private void setupTable() {
        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));
        colName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        colContact.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getContact()));
        colPhotoUrl.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhotoUrl()));

        sponsorTable.setItems(sponsorData);
    }

    private void loadSponsors() {
        sponsorData.setAll(sponsorRepository.findAll());
    }

    /** Fills the form with the data of the selected sponsor. */
    private void showSponsorInForm(Sponsor sponsor) {
        currentSponsor = sponsor;
        if (sponsor == null) {
            clearForm();
            return;
        }
        nameField.setText(sponsor.getName());
        contactField.setText(sponsor.getContact());
        photoUrlField.setText(sponsor.getPhotoUrl());
        statusLabel.setText("");
    }

    private void clearForm() {
        nameField.clear();
        contactField.clear();
        photoUrlField.clear();
        statusLabel.setText("");
    }

    /** "Neu" button: clears the form so a new Sponsor can be created. */
    @FXML
    private void onNew() {
        currentSponsor = null;
        sponsorTable.getSelectionModel().clearSelection();
        clearForm();
    }

    /** "Speichern" button: creates a new Sponsor or updates the selected one. */
    @FXML
    private void onSave() {
        String name = nameField.getText();
        if (name == null || name.isBlank()) {
            statusLabel.setText("Name darf nicht leer sein.");
            return;
        }

        Sponsor sponsor = (currentSponsor != null) ? currentSponsor : new Sponsor();
        sponsor.setName(name);
        sponsor.setContact(contactField.getText());
        sponsor.setPhotoUrl(photoUrlField.getText());

        try {
            sponsorRepository.save(sponsor);
            statusLabel.setText("Gespeichert.");
            loadSponsors();
            onNew();
        } catch (Exception e) {
            statusLabel.setText("Fehler beim Speichern: " + e.getMessage());
        }
    }

    /** "Löschen" button: deletes the selected Sponsor. */
    @FXML
    private void onDelete() {
        if (currentSponsor == null || currentSponsor.getId() == null) {
            statusLabel.setText("Bitte zuerst einen Sponsor auswählen.");
            return;
        }

        try {
            sponsorRepository.delete(currentSponsor);
            statusLabel.setText("Gelöscht.");
            loadSponsors();
            onNew();
        } catch (Exception e) {
            statusLabel.setText("Fehler beim Löschen: " + e.getMessage());
        }
    }
}

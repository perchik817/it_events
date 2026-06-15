package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.scene.layout.VBox;
import whz.it_events.it_eventsdbapp.SessionContext;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.EventRepository;
import whz.it_events.it_eventsdbapp.dao.EventSponsorRepository;
import whz.it_events.it_eventsdbapp.dao.SponsorRepository;
import whz.it_events.it_eventsdbapp.model.*;

public class EventSponsorController {
    @FXML private TableView<EventSponsor> eventSponsorTable;
    @FXML private TableColumn<EventSponsor, Long> colId;
    @FXML private TableColumn<EventSponsor, String> colEvent;
    @FXML private TableColumn<EventSponsor, String> colSponsor;
    @FXML private TableColumn<EventSponsor, String> colFee;
    @FXML private ComboBox<Event> eventComboBox;
    @FXML private ComboBox<Sponsor> sponsorComboBox;
    @FXML private TextField feeField;
    @FXML private VBox rightPanel;
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private EntityManager em;
    private EventSponsorRepository eventSponsorRepo;
    private EventRepository eventRepo;
    private SponsorRepository sponsorRepo;
    private final ObservableList<EventSponsor> data = FXCollections.observableArrayList();
    private EventSponsor current;

    @FXML public void initialize() {
        em = JpaUtil.getEntityManager();
        eventSponsorRepo = new EventSponsorRepository(em, EventSponsor.class);
        eventRepo = new EventRepository(em, Event.class);
        sponsorRepo = new SponsorRepository(em, Sponsor.class);

        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colEvent.setCellValueFactory(c -> { Event e = c.getValue().getEvent(); return new SimpleStringProperty(e != null ? e.getName() : ""); });
        colSponsor.setCellValueFactory(c -> { Sponsor s = c.getValue().getSponsor(); return new SimpleStringProperty(s != null ? s.getName() : ""); });
        colFee.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFee()));
        eventSponsorTable.setItems(data);

        eventComboBox.setItems(FXCollections.observableArrayList(eventRepo.findAll()));
        eventComboBox.setConverter(new StringConverter<Event>() {
            @Override public String toString(Event e) { return e != null ? e.getName() : ""; }
            @Override public Event fromString(String s) { return null; }
        });
        sponsorComboBox.setItems(FXCollections.observableArrayList(sponsorRepo.findAll()));
        sponsorComboBox.setConverter(new StringConverter<Sponsor>() {
            @Override public String toString(Sponsor s) { return s != null ? s.getName() : ""; }
            @Override public Sponsor fromString(String s) { return null; }
        });

        eventSponsorTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> showInForm(n));
        onNew();
        applyRoleAccess(); load();
    }

    private void load() { data.setAll(eventSponsorRepo.findAll()); }
    private void showInForm(EventSponsor es) { current = es; if (es == null) { clearForm(); return; } eventComboBox.setValue(es.getEvent()); sponsorComboBox.setValue(es.getSponsor()); feeField.setText(es.getFee()); statusLabel.setText(""); }
    private void clearForm() { eventComboBox.setValue(null); sponsorComboBox.setValue(null); feeField.clear(); statusLabel.setText(""); }

    @FXML private void onNew() { current = null; eventSponsorTable.getSelectionModel().clearSelection(); clearForm(); }
    @FXML private void onSave() {
        Event event = eventComboBox.getValue();
        Sponsor sponsor = sponsorComboBox.getValue();
        if (event == null || sponsor == null) { statusLabel.setText("Event und Sponsor sind Pflichtfelder."); return; }
        EventSponsor es = (current != null) ? current : new EventSponsor(feeField.getText(), event, sponsor);
        if (current != null) { es.setEvent(event); es.setSponsor(sponsor); es.setFee(feeField.getText()); }
        try { eventSponsorRepo.save(es); statusLabel.setText("Gespeichert."); load(); onNew();
        applyRoleAccess(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }
    @FXML private void onDelete() {
        if (current == null) { statusLabel.setText("Bitte zuerst auswählen."); return; }
        try { eventSponsorRepo.delete(current); statusLabel.setText("Gelöscht."); load(); onNew();
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

package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.EventRepository;
import whz.it_events.it_eventsdbapp.dao.OrganisatorRepository;
import whz.it_events.it_eventsdbapp.dao.UserRepository;
import whz.it_events.it_eventsdbapp.model.*;

public class OrganisatorController {
    @FXML private TableView<Organisator> organisatorTable;
    @FXML private TableColumn<Organisator, Long> colId;
    @FXML private TableColumn<Organisator, String> colUser;
    @FXML private TableColumn<Organisator, String> colEvent;
    @FXML private TableColumn<Organisator, String> colWorkArea;
    @FXML private ComboBox<User> userComboBox;
    @FXML private ComboBox<Event> eventComboBox;
    @FXML private TextField workAreaField;
    @FXML private Label statusLabel;

    private EntityManager em;
    private OrganisatorRepository organisatorRepo;
    private UserRepository userRepo;
    private EventRepository eventRepo;
    private final ObservableList<Organisator> data = FXCollections.observableArrayList();
    private Organisator current;

    @FXML public void initialize() {
        em = JpaUtil.getEntityManager();
        organisatorRepo = new OrganisatorRepository(em, Organisator.class);
        userRepo = new UserRepository(em, User.class);
        eventRepo = new EventRepository(em, Event.class);

        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colUser.setCellValueFactory(c -> { User u = c.getValue().getUser(); return new SimpleStringProperty(u != null ? u.getName() + " " + u.getLastname() : ""); });
        colEvent.setCellValueFactory(c -> { Event e = c.getValue().getEvent(); return new SimpleStringProperty(e != null ? e.getName() : ""); });
        colWorkArea.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getWorkArea()));
        organisatorTable.setItems(data);

        userComboBox.setItems(FXCollections.observableArrayList(userRepo.findAll()));
        userComboBox.setConverter(new StringConverter<User>() {
            @Override public String toString(User u) { return u != null ? u.getName() + " " + u.getLastname() : ""; }
            @Override public User fromString(String s) { return null; }
        });
        eventComboBox.setItems(FXCollections.observableArrayList(eventRepo.findAll()));
        eventComboBox.setConverter(new StringConverter<Event>() {
            @Override public String toString(Event e) { return e != null ? e.getName() : ""; }
            @Override public Event fromString(String s) { return null; }
        });

        organisatorTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> showInForm(n));
        onNew(); load();
    }

    private void load() { data.setAll(organisatorRepo.findAll()); }
    private void showInForm(Organisator o) { current = o; if (o == null) { clearForm(); return; } userComboBox.setValue(o.getUser()); eventComboBox.setValue(o.getEvent()); workAreaField.setText(o.getWorkArea()); statusLabel.setText(""); }
    private void clearForm() { userComboBox.setValue(null); eventComboBox.setValue(null); workAreaField.clear(); statusLabel.setText(""); }

    @FXML private void onNew() { current = null; organisatorTable.getSelectionModel().clearSelection(); clearForm(); }
    @FXML private void onSave() {
        User user = userComboBox.getValue();
        if (user == null) { statusLabel.setText("Bitte einen User auswählen."); return; }
        Organisator o = (current != null) ? current : new Organisator(user, eventComboBox.getValue(), workAreaField.getText());
        if (current != null) { o.setUser(user); o.setEvent(eventComboBox.getValue()); o.setWorkArea(workAreaField.getText()); }
        try { organisatorRepo.save(o); statusLabel.setText("Gespeichert."); load(); onNew(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }
    @FXML private void onDelete() {
        if (current == null) { statusLabel.setText("Bitte zuerst auswählen."); return; }
        try { organisatorRepo.delete(current); statusLabel.setText("Gelöscht."); load(); onNew(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }
}

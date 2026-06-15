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
import whz.it_events.it_eventsdbapp.dao.SessionRepository;
import whz.it_events.it_eventsdbapp.dao.UserRepository;
import whz.it_events.it_eventsdbapp.dao.VisitorRepository;
import whz.it_events.it_eventsdbapp.model.*;

public class VisitorController {
    @FXML private TableView<Visitor> visitorTable;
    @FXML private TableColumn<Visitor, Long> colId;
    @FXML private TableColumn<Visitor, String> colUser;
    @FXML private TableColumn<Visitor, String> colSession;
    @FXML private ComboBox<User> userComboBox;
    @FXML private ComboBox<Session> sessionComboBox;
    @FXML private VBox rightPanel;
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private EntityManager em;
    private VisitorRepository visitorRepo;
    private UserRepository userRepo;
    private SessionRepository sessionRepo;
    private final ObservableList<Visitor> data = FXCollections.observableArrayList();
    private Visitor current;

    @FXML public void initialize() {
        em = JpaUtil.getEntityManager();
        visitorRepo = new VisitorRepository(em, Visitor.class);
        userRepo = new UserRepository(em, User.class);
        sessionRepo = new SessionRepository(em, Session.class);

        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colUser.setCellValueFactory(c -> { User u = c.getValue().getUser(); return new SimpleStringProperty(u != null ? u.getName() + " " + u.getLastname() : ""); });
        colSession.setCellValueFactory(c -> { Session s = c.getValue().getSession(); return new SimpleStringProperty(s != null ? s.getTitel() : ""); });
        visitorTable.setItems(data);

        userComboBox.setItems(FXCollections.observableArrayList(userRepo.findAll()));
        userComboBox.setConverter(new StringConverter<User>() {
            @Override public String toString(User u) { return u != null ? u.getName() + " " + u.getLastname() : ""; }
            @Override public User fromString(String s) { return null; }
        });
        sessionComboBox.setItems(FXCollections.observableArrayList(sessionRepo.findAll()));
        sessionComboBox.setConverter(new StringConverter<Session>() {
            @Override public String toString(Session s) { return s != null ? s.getTitel() : ""; }
            @Override public Session fromString(String s) { return null; }
        });

        visitorTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> showInForm(n));
        onNew();
        applyRoleAccess(); load();
    }

    private void load() { data.setAll(visitorRepo.findAll()); }
    private void showInForm(Visitor v) { current = v; if (v == null) { clearForm(); return; } userComboBox.setValue(v.getUser()); sessionComboBox.setValue(v.getSession()); statusLabel.setText(""); }
    private void clearForm() { userComboBox.setValue(null); sessionComboBox.setValue(null); statusLabel.setText(""); }

    @FXML private void onNew() { current = null; visitorTable.getSelectionModel().clearSelection(); clearForm(); }
    @FXML private void onSave() {
        User user = userComboBox.getValue();
        Session session = sessionComboBox.getValue();
        if (user == null || session == null) { statusLabel.setText("User und Session sind Pflichtfelder."); return; }
        Visitor v = (current != null) ? current : new Visitor(user, session);
        if (current != null) { v.setUser(user); v.setSession(session); }
        try { visitorRepo.save(v); statusLabel.setText("Gespeichert."); load(); onNew();
        applyRoleAccess(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }
    @FXML private void onDelete() {
        if (current == null) { statusLabel.setText("Bitte zuerst auswählen."); return; }
        try { visitorRepo.delete(current); statusLabel.setText("Gelöscht."); load(); onNew();
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

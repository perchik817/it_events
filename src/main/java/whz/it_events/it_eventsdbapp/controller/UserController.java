package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.UserRepository;
import whz.it_events.it_eventsdbapp.model.User;
import whz.it_events.it_eventsdbapp.model.enums.Role;

import java.time.LocalDateTime;

public class UserController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Long> colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colLastname;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;

    @FXML private TextField nameField;
    @FXML private TextField lastnameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private EntityManager entityManager;
    private UserRepository userRepository;

    private final ObservableList<User> userData = FXCollections.observableArrayList();

    // currently selected/edited user; null = "Neu" (creating a new one)
    private User currentUser;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        userRepository = new UserRepository(entityManager, User.class);

        setupTable();
        setupRoleComboBox();
        loadUsers();

        userTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showUserInForm(newVal)
        );

        onNew();
    }

    private void setupTable() {
        colId.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId()));
        colName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        colLastname.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLastname()));
        colEmail.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmail()));
        colRole.setCellValueFactory(cellData -> {
            Role role = cellData.getValue().getRole();
            return new SimpleStringProperty(role != null ? role.toString() : "");
        });

        userTable.setItems(userData);
    }

    private void setupRoleComboBox() {
        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
    }

    private void loadUsers() {
        userData.setAll(userRepository.findAll());
    }

    /** Fills the form with the data of the selected user. */
    private void showUserInForm(User user) {
        currentUser = user;
        if (user == null) {
            clearForm();
            return;
        }
        nameField.setText(user.getName());
        lastnameField.setText(user.getLastname());
        emailField.setText(user.getEmail());
        roleComboBox.setValue(user.getRole());
        passwordField.setText(user.getPassword());
        statusLabel.setText("");
    }

    private void clearForm() {
        nameField.clear();
        lastnameField.clear();
        emailField.clear();
        roleComboBox.setValue(null);
        passwordField.clear();
        statusLabel.setText("");
    }

    /** "Neu" button: clears the form so a new User can be created. */
    @FXML
    private void onNew() {
        currentUser = null;
        userTable.getSelectionModel().clearSelection();
        clearForm();
    }

    /** "Speichern" button: creates a new User or updates the selected one. */
    @FXML
    private void onSave() {
        String name = nameField.getText();
        if (name == null || name.isBlank()) {
            statusLabel.setText("Name darf nicht leer sein.");
            return;
        }

        String email = emailField.getText();
        if (email == null || email.isBlank()) {
            statusLabel.setText("Email darf nicht leer sein.");
            return;
        }

        Role role = roleComboBox.getValue();
        if (role == null) {
            statusLabel.setText("Bitte eine Rolle auswählen.");
            return;
        }

        boolean isNew = (currentUser == null);
        User user = isNew ? new User() : currentUser;
        user.setName(name);
        user.setLastname(lastnameField.getText());
        user.setEmail(email);
        user.setRole(role);
        user.setPassword(passwordField.getText());
        if (isNew) {
            user.setRegistrationDate(LocalDateTime.now());
        }

        try {
            userRepository.save(user);
            statusLabel.setText("Gespeichert.");
            loadUsers();
            onNew();
        } catch (Exception e) {
            statusLabel.setText("Fehler beim Speichern: " + e.getMessage());
        }
    }

    /** "Löschen" button: deletes the selected User. */
    @FXML
    private void onDelete() {
        if (currentUser == null || currentUser.getId() == null) {
            statusLabel.setText("Bitte zuerst einen User auswählen.");
            return;
        }

        try {
            userRepository.delete(currentUser);
            statusLabel.setText("Gelöscht.");
            loadUsers();
            onNew();
        } catch (Exception e) {
            statusLabel.setText("Fehler beim Löschen: " + e.getMessage());
        }
    }
}

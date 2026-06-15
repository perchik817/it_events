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
import whz.it_events.it_eventsdbapp.service.RegisterService;
import whz.it_events.it_eventsdbapp.service.UserService;
import whz.it_events.it_eventsdbapp.service.ValidationService;

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
    private UserService userService;
    private RegisterService registerService;

    private final ObservableList<User> userData = FXCollections.observableArrayList();
    private User currentUser;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        userRepository = new UserRepository(entityManager, User.class);
        userService = new UserService(userRepository);
        registerService = new RegisterService(userService, new ValidationService());

        setupTable();
        setupRoleComboBox();
        loadUsers();

        userTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showUserInForm(newVal)
        );

        onNew();
    }

    private void setupTable() {
        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colLastname.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastname()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colRole.setCellValueFactory(c -> {
            Role role = c.getValue().getRole();
            return new SimpleStringProperty(role != null ? role.toString() : "");
        });
        userTable.setItems(userData);
    }

    private void setupRoleComboBox() {
        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
    }

    private void loadUsers() {
        entityManager.clear();
        userData.setAll(userRepository.findAll());
    }

    private void showUserInForm(User user) {
        currentUser = user;
        if (user == null) { clearForm(); return; }
        nameField.setText(user.getName());
        lastnameField.setText(user.getLastname());
        emailField.setText(user.getEmail());
        roleComboBox.setValue(user.getRole());
        passwordField.clear();
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

    @FXML
    private void onNew() {
        currentUser = null;
        userTable.getSelectionModel().clearSelection();
        clearForm();
    }

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

        try {
            if (currentUser == null) {
                // NEW user → RegisterService hashes password and saves with Role.USER
                String password = passwordField.getText();
                if (password == null || password.isBlank()) {
                    statusLabel.setText("Passwort darf nicht leer sein.");
                    return;
                }
                User newUser = registerService.register(
                        name, lastnameField.getText(), email, password
                );
                // if role is not USER — update role with merge (no second INSERT)
                if (role != Role.USER) {
                    newUser.setRole(role);
                    userRepository.save(newUser);
                }
            } else {
                // EDIT existing user → update fields only
                currentUser.setName(name);
                currentUser.setLastname(lastnameField.getText());
                currentUser.setEmail(email);
                currentUser.setRole(role);
                if (!passwordField.getText().isBlank()) {
                    statusLabel.setText("Passwort kann nur beim Erstellen gesetzt werden.");
                    return;
                }
                userRepository.save(currentUser);
            }
            statusLabel.setText("Gespeichert.");
            loadUsers();
            onNew();
        } catch (Exception e) {
            statusLabel.setText("Fehler: " + e.getMessage());
        }
    }

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

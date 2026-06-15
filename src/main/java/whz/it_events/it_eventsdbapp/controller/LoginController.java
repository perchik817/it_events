package whz.it_events.it_eventsdbapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import whz.it_events.it_eventsdbapp.SessionContext;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.UserRepository;
import whz.it_events.it_eventsdbapp.model.User;
import whz.it_events.it_eventsdbapp.model.enums.Role;
import whz.it_events.it_eventsdbapp.service.AuthService;
import whz.it_events.it_eventsdbapp.service.RegisterService;
import whz.it_events.it_eventsdbapp.service.UserService;
import whz.it_events.it_eventsdbapp.service.ValidationService;

import java.io.IOException;

public class LoginController {

    // Login panel
    @FXML private VBox loginPanel;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    // Register panel
    @FXML private VBox registerPanel;
    @FXML private TextField regNameField;
    @FXML private TextField regLastnameField;
    @FXML private TextField regEmailField;
    @FXML private PasswordField regPasswordField;
    @FXML private Label regErrorLabel;

    private AuthService authService;
    private RegisterService registerService;
    private UserRepository userRepo;

    @FXML
    public void initialize() {
        userRepo = new UserRepository(JpaUtil.getEntityManager(), User.class);
        UserService userService = new UserService(userRepo);
        ValidationService validationService = new ValidationService();
        authService = new AuthService(userService, validationService);
        registerService = new RegisterService(userService, validationService);

        createDefaultUser("Admin", "Test", "admin@test.com", "admin123", Role.ADMIN);
        createDefaultUser("Jury", "Test", "jury@test.com", "jury123", Role.JURY);
        createDefaultUser("User", "Test", "user@test.com", "user123", Role.USER);
    }

    private void createDefaultUser(String name, String lastname, String email,
                                   String password, Role role) {
        try {
            UserService userService = new UserService(userRepo);
            if (!userService.existsByEmail(email)) {
                String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
                User user = new User(name, lastname, email, role, hashed);
                userRepo.save(user);
            }
        } catch (Exception e) {
            System.out.println("User init error: " + e.getMessage());
        }
    }

    // --- Login ---

    @FXML
    private void onLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        try {
            User user = authService.login(email, password);
            SessionContext.setCurrentUser(user);
            if (user.getRole() == Role.ADMIN) {
                openWindow("/whz/it_events/it_eventsdbapp/admin-view.fxml",
                        "IT Events - Admin (" + user.getName() + ")", true);
            } else {
                openWindow("/whz/it_events/it_eventsdbapp/dashboard-view.fxml",
                        "IT Events - Dashboard (" + user.getName() + " · " + user.getRole() + ")", true);
            }
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    // --- Register ---

    @FXML
    private void onRegister() {
        String name = regNameField.getText();
        String lastname = regLastnameField.getText();
        String email = regEmailField.getText();
        String password = regPasswordField.getText();

        try {
            registerService.register(name, lastname, email, password);
            regErrorLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 12px;");
            regErrorLabel.setText("✓ Konto erstellt! Sie können sich jetzt anmelden.");
            // switch back to login after short delay
            emailField.setText(email);
            showLogin();
        } catch (Exception e) {
            regErrorLabel.setStyle("-fx-text-fill: #E2574C; -fx-font-size: 12px;");
            regErrorLabel.setText(e.getMessage());
        }
    }

    // --- Panel switching ---

    @FXML
    private void showRegister() {
        loginPanel.setVisible(false);
        loginPanel.setManaged(false);
        registerPanel.setVisible(true);
        registerPanel.setManaged(true);
        regNameField.clear();
        regLastnameField.clear();
        regEmailField.clear();
        regPasswordField.clear();
        regErrorLabel.setText("");
    }

    @FXML
    private void showLogin() {
        registerPanel.setVisible(false);
        registerPanel.setManaged(false);
        loginPanel.setVisible(true);
        loginPanel.setManaged(true);
        errorLabel.setText("");
    }

    private void openWindow(String fxmlPath, String title, boolean maximized) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) emailField.getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource("/whz/it_events/it_eventsdbapp/styles.css").toExternalForm()
        );
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMaximized(maximized);
        stage.show();
    }
}

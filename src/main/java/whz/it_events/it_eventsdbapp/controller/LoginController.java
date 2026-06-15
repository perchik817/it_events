package whz.it_events.it_eventsdbapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import whz.it_events.it_eventsdbapp.SessionContext;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.UserRepository;
import whz.it_events.it_eventsdbapp.model.User;
import whz.it_events.it_eventsdbapp.model.enums.Role;
import whz.it_events.it_eventsdbapp.service.AuthService;
import whz.it_events.it_eventsdbapp.service.UserService;
import whz.it_events.it_eventsdbapp.service.ValidationService;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private AuthService authService;
    private UserService userService;
    private UserRepository userRepo;

    @FXML
    public void initialize() {
        userRepo = new UserRepository(JpaUtil.getEntityManager(), User.class);
        userService = new UserService(userRepo);
        ValidationService validationService = new ValidationService();
        authService = new AuthService(userService, validationService);

        // create default users if not exist
        createDefaultUser("Admin", "Test", "admin@test.com", "admin123", Role.ADMIN);
        createDefaultUser("Jury", "Test", "jury@test.com", "jury123", Role.JURY);
        createDefaultUser("User", "Test", "user@test.com", "user123", Role.USER);
    }

    private void createDefaultUser(String name, String lastname, String email,
                                   String password, Role role) {
        try {
            if (!userService.existsByEmail(email)) {
                String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
                User user = new User(name, lastname, email, role, hashed);
                userRepo.save(user);
                System.out.println("✓ Created: " + email + " / " + password + " [" + role + "]");
            }
        } catch (Exception e) {
            System.out.println("User init error (" + email + "): " + e.getMessage());
        }
    }

    @FXML
    private void onLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            User user = authService.login(email, password);
            SessionContext.setCurrentUser(user);
            openMainWindow();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private void openMainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/whz/it_events/it_eventsdbapp/main-view.fxml")
        );
        Parent root = loader.load();

        Stage stage = (Stage) emailField.getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource("/whz/it_events/it_eventsdbapp/styles.css").toExternalForm()
        );

        stage.setTitle("IT Events - Verwaltung ("
                + SessionContext.getCurrentUser().getName() + " · "
                + SessionContext.getRole() + ")");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}

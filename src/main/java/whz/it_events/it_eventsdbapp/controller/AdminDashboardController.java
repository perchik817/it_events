package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import whz.it_events.it_eventsdbapp.SessionContext;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.EventRepository;
import whz.it_events.it_eventsdbapp.dao.ScoreRepository;
import whz.it_events.it_eventsdbapp.dao.SubmissionRepository;
import whz.it_events.it_eventsdbapp.dao.TeamRepository;
import whz.it_events.it_eventsdbapp.dao.UserRepository;
import whz.it_events.it_eventsdbapp.model.Event;
import whz.it_events.it_eventsdbapp.model.Score;
import whz.it_events.it_eventsdbapp.model.Submission;
import whz.it_events.it_eventsdbapp.model.Team;
import whz.it_events.it_eventsdbapp.model.User;

import java.io.IOException;
import java.util.List;

public class AdminDashboardController {

    @FXML private Label adminNameLabel;
    @FXML private Label adminEmailLabel;
    @FXML private Label pageTitle;
    @FXML private Label statEventsNum;
    @FXML private Label statUsersNum;
    @FXML private Label statTeamsNum;
    @FXML private Label statSubmissionsNum;
    @FXML private Label statScoresNum;
    @FXML private VBox recentEventsList;
    @FXML private VBox mainContent;

    @FXML private Button btnOverview;
    @FXML private Button btnEvents;
    @FXML private Button btnUsers;
    @FXML private Button btnAll;

    private EntityManager em;
    private EventRepository eventRepo;
    private UserRepository userRepo;
    private TeamRepository teamRepo;
    private SubmissionRepository submissionRepo;
    private ScoreRepository scoreRepo;

    private static final String ACTIVE =
            "-fx-background-color: #5B6EF5; -fx-text-fill: white; -fx-background-radius: 10; " +
            "-fx-padding: 11 14; -fx-alignment: CENTER-LEFT; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 13px;";
    private static final String INACTIVE =
            "-fx-background-color: transparent; -fx-text-fill: #8A8FA3; -fx-background-radius: 10; " +
            "-fx-padding: 11 14; -fx-alignment: CENTER-LEFT; -fx-cursor: hand; -fx-font-size: 13px;";

    @FXML
    public void initialize() {
        em = JpaUtil.getEntityManager();
        eventRepo = new EventRepository(em, Event.class);
        userRepo = new UserRepository(em, User.class);
        teamRepo = new TeamRepository(em, Team.class);
        submissionRepo = new SubmissionRepository(em, Submission.class);
        scoreRepo = new ScoreRepository(em, Score.class);

        User user = SessionContext.getCurrentUser();
        adminNameLabel.setText(user.getName() + " " + user.getLastname());
        adminEmailLabel.setText(user.getEmail());

        showOverview();
    }

    @FXML
    public void showOverview() {
        setActive(btnOverview);
        pageTitle.setText("Übersicht");

        // Stats
        List<Event> events = eventRepo.findAll();
        List<User> users = userRepo.findAll();
        List<Team> teams = teamRepo.findAll();
        List<Submission> submissions = submissionRepo.findAll();
        List<Score> scores = scoreRepo.findAll();

        statEventsNum.setText(String.valueOf(events.size()));
        statUsersNum.setText(String.valueOf(users.size()));
        statTeamsNum.setText(String.valueOf(teams.size()));
        statSubmissionsNum.setText(String.valueOf(submissions.size()));
        statScoresNum.setText(String.valueOf(scores.size()));

        // Recent Events
        recentEventsList.getChildren().clear();
        List<Event> recent = eventRepo.findAllOrderedByStartDate();
        int count = Math.min(recent.size(), 5);
        for (int i = 0; i < count; i++) {
            Event event = recent.get(i);
            HBox row = new HBox(12);
            row.setStyle("-fx-alignment: CENTER-LEFT; -fx-padding: 8 0;");

            VBox dot = new VBox();
            dot.setStyle("-fx-background-color: #5B6EF5; -fx-background-radius: 50; -fx-min-width: 8; -fx-min-height: 8; -fx-max-width: 8; -fx-max-height: 8;");
            dot.setTranslateY(4);

            VBox info = new VBox(2);
            Label name = new Label(event.getName());
            name.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");
            String loc = event.getLocation() != null ? event.getLocation().getLocationName() : "–";
            String start = event.getStartDate() != null ? event.getStartDate().toLocalDate().toString() : "–";
            Label sub = new Label("📍 " + loc + "  •  📅 " + start);
            sub.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3;");
            info.getChildren().addAll(name, sub);

            Label badge = new Label(event.getStatus() != null ? event.getStatus().toString() : "");
            badge.setStyle("-fx-background-color: #EEF0FF; -fx-text-fill: #5B6EF5; " +
                    "-fx-background-radius: 6; -fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold;");
            badge.setTranslateX(8);

            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            row.getChildren().addAll(dot, info, spacer, badge);

            if (i < count - 1) {
                VBox wrapper = new VBox();
                wrapper.getChildren().add(row);
                javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
                sep.setStyle("-fx-background-color: #F4F6FB;");
                wrapper.getChildren().add(sep);
                recentEventsList.getChildren().add(wrapper);
            } else {
                recentEventsList.getChildren().add(row);
            }
        }

        if (recent.isEmpty()) {
            Label empty = new Label("Keine Events gefunden.");
            empty.setStyle("-fx-text-fill: #8A8FA3;");
            recentEventsList.getChildren().add(empty);
        }
    }

    @FXML
    public void showAdminEvents() {
        setActive(btnEvents);
        pageTitle.setText("Events");
        recentEventsList.getChildren().clear();
        List<Event> events = eventRepo.findAllOrderedByStartDate();
        for (Event event : events) {
            HBox row = new HBox(12);
            row.setStyle("-fx-alignment: CENTER-LEFT; -fx-padding: 10 0;");
            Label name = new Label(event.getName());
            name.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2E3142; -fx-min-width: 200;");
            String loc = event.getLocation() != null ? event.getLocation().getLocationName() : "–";
            Label location = new Label("📍 " + loc);
            location.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3; -fx-min-width: 160;");
            String start = event.getStartDate() != null ? event.getStartDate().toLocalDate().toString() : "–";
            Label date = new Label("📅 " + start);
            date.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3;");
            row.getChildren().addAll(name, location, date);
            recentEventsList.getChildren().add(row);
            javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
            sep.setStyle("-fx-background-color: #F4F6FB;");
            recentEventsList.getChildren().add(sep);
        }
    }

    @FXML
    public void showAdminUsers() {
        setActive(btnUsers);
        pageTitle.setText("Users");
        recentEventsList.getChildren().clear();
        List<User> users = userRepo.findAll();
        for (User user : users) {
            HBox row = new HBox(12);
            row.setStyle("-fx-alignment: CENTER-LEFT; -fx-padding: 10 0;");
            Label name = new Label(user.getName() + " " + user.getLastname());
            name.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2E3142; -fx-min-width: 200;");
            Label email = new Label("✉️ " + user.getEmail());
            email.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3; -fx-min-width: 220;");
            Label role = new Label(user.getRole() != null ? user.getRole().toString() : "");
            role.setStyle("-fx-background-color: #EEF0FF; -fx-text-fill: #5B6EF5; " +
                    "-fx-background-radius: 6; -fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold;");
            row.getChildren().addAll(name, email, role);
            recentEventsList.getChildren().add(row);
            javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
            sep.setStyle("-fx-background-color: #F4F6FB;");
            recentEventsList.getChildren().add(sep);
        }
    }

    @FXML
    public void openFullAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/whz/it_events/it_eventsdbapp/main-view.fxml")
            );
            Parent root = loader.load();
            Stage stage = (Stage) adminNameLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/whz/it_events/it_eventsdbapp/styles.css").toExternalForm()
            );
            stage.setTitle("IT Events - Verwaltung (Admin · ADMIN)");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onLogout() {
        try {
            SessionContext.clear();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/whz/it_events/it_eventsdbapp/login-view.fxml")
            );
            Parent root = loader.load();
            Stage stage = (Stage) adminNameLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/whz/it_events/it_eventsdbapp/styles.css").toExternalForm()
            );
            stage.setTitle("IT Events - Anmeldung");
            stage.setMaximized(false);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActive(Button active) {
        for (Button btn : new Button[]{btnOverview, btnEvents, btnUsers, btnAll}) {
            if (btn != null) btn.setStyle(INACTIVE);
        }
        active.setStyle(ACTIVE);
    }
}

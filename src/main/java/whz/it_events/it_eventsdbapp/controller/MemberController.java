package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.scene.layout.VBox;
import whz.it_events.it_eventsdbapp.SessionContext;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.MemberRepository;
import whz.it_events.it_eventsdbapp.dao.TeamRepository;
import whz.it_events.it_eventsdbapp.dao.UserRepository;
import whz.it_events.it_eventsdbapp.model.Member;
import whz.it_events.it_eventsdbapp.model.Team;
import whz.it_events.it_eventsdbapp.model.User;

public class MemberController {

    @FXML private TableView<Member> memberTable;
    @FXML private TableColumn<Member, Long> colId;
    @FXML private TableColumn<Member, String> colUser;
    @FXML private TableColumn<Member, String> colTeam;
    @FXML private TableColumn<Member, String> colRole;

    @FXML private ComboBox<User> userComboBox;
    @FXML private ComboBox<Team> teamComboBox;
    @FXML private TextField roleField;
    @FXML private VBox rightPanel;
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private EntityManager entityManager;
    private MemberRepository memberRepository;
    private UserRepository userRepository;
    private TeamRepository teamRepository;

    private final ObservableList<Member> data = FXCollections.observableArrayList();
    private Member current;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        memberRepository = new MemberRepository(entityManager, Member.class);
        userRepository = new UserRepository(entityManager, User.class);
        teamRepository = new TeamRepository(entityManager, Team.class);

        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colUser.setCellValueFactory(c -> {
            User u = c.getValue().getUser();
            return new SimpleStringProperty(u != null ? u.getName() + " " + u.getLastname() : "");
        });
        colTeam.setCellValueFactory(c -> {
            Team t = c.getValue().getTeam();
            return new SimpleStringProperty(t != null ? t.getName() : "");
        });
        colRole.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTeamRole()));

        memberTable.setItems(data);

        userComboBox.setItems(FXCollections.observableArrayList(userRepository.findAll()));
        userComboBox.setConverter(new StringConverter<User>() {
            @Override public String toString(User u) { return u != null ? u.getName() + " " + u.getLastname() : ""; }
            @Override public User fromString(String s) { return null; }
        });

        teamComboBox.setItems(FXCollections.observableArrayList(teamRepository.findAll()));
        teamComboBox.setConverter(new StringConverter<Team>() {
            @Override public String toString(Team t) { return t != null ? t.getName() : ""; }
            @Override public Team fromString(String s) { return null; }
        });

        memberTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> showInForm(n));
        onNew();
        applyRoleAccess();
        load();
    }

    private void load() { data.setAll(memberRepository.findAll()); }

    private void showInForm(Member m) {
        current = m;
        if (m == null) { clearForm(); return; }
        userComboBox.setValue(m.getUser());
        teamComboBox.setValue(m.getTeam());
        roleField.setText(m.getTeamRole());
        statusLabel.setText("");
    }

    private void clearForm() {
        userComboBox.setValue(null);
        teamComboBox.setValue(null);
        roleField.clear();
        statusLabel.setText("");
    }

    @FXML private void onNew() {
        current = null;
        memberTable.getSelectionModel().clearSelection();
        clearForm();
    }

    @FXML private void onSave() {
        User user = userComboBox.getValue();
        Team team = teamComboBox.getValue();
        if (user == null || team == null) {
            statusLabel.setText("User und Team sind Pflichtfelder.");
            return;
        }
        Member m = (current != null) ? current : new Member(team, roleField.getText(), user);
        if (current != null) {
            m.setUser(user);
            m.setTeam(team);
            m.setTeamRole(roleField.getText());
        }
        try { memberRepository.save(m); statusLabel.setText("Gespeichert."); load(); onNew();
        applyRoleAccess(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }

    @FXML private void onDelete() {
        if (current == null) { statusLabel.setText("Bitte zuerst auswählen."); return; }
        try { memberRepository.delete(current); statusLabel.setText("Gelöscht."); load(); onNew();
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

package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import whz.it_events.it_eventsdbapp.SessionContext;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.EventRepository;
import whz.it_events.it_eventsdbapp.dao.JuryRepository;
import whz.it_events.it_eventsdbapp.dao.ParticipantRepository;
import whz.it_events.it_eventsdbapp.dao.SessionRepository;
import whz.it_events.it_eventsdbapp.dao.TeamRepository;
import whz.it_events.it_eventsdbapp.dao.TrackRepository;
import whz.it_events.it_eventsdbapp.dao.MemberRepository;
import whz.it_events.it_eventsdbapp.dao.SubmissionRepository;
import whz.it_events.it_eventsdbapp.dao.ScoreRepository;
import whz.it_events.it_eventsdbapp.dao.VisitorRepository;
import whz.it_events.it_eventsdbapp.model.Event;
import whz.it_events.it_eventsdbapp.model.Jury;
import whz.it_events.it_eventsdbapp.model.Participant;
import whz.it_events.it_eventsdbapp.model.Session;
import whz.it_events.it_eventsdbapp.model.Team;
import whz.it_events.it_eventsdbapp.model.Track;
import whz.it_events.it_eventsdbapp.model.User;
import whz.it_events.it_eventsdbapp.model.Member;
import whz.it_events.it_eventsdbapp.model.Score;
import whz.it_events.it_eventsdbapp.model.Submission;
import whz.it_events.it_eventsdbapp.model.SubmissionTeam;
import whz.it_events.it_eventsdbapp.model.Visitor;
import whz.it_events.it_eventsdbapp.model.enums.ParticipationType;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Label userEmailLabel;
    @FXML private Label contentTitle;
    @FXML private VBox listContainer;
    @FXML private ScrollPane detailPanel;
    @FXML private VBox detailContent;

    @FXML private Button btnEvents;
    @FXML private Button btnSessions;
    @FXML private Button btnTeams;
    @FXML private Button btnScores;

    private EntityManager em;
    private EventRepository eventRepo;
    private SessionRepository sessionRepo;
    private TeamRepository teamRepo;
    private TrackRepository trackRepo;
    private JuryRepository juryRepo;
    private ParticipantRepository participantRepo;
    private VisitorRepository visitorRepo;
    private ScoreRepository scoreRepo;
    private MemberRepository memberRepo;
    private SubmissionRepository submissionRepo;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private static final String ACTIVE_STYLE =
            "-fx-background-color: #5B6EF5; -fx-text-fill: white; -fx-background-radius: 10; " +
            "-fx-padding: 12 16; -fx-alignment: CENTER-LEFT; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String INACTIVE_STYLE =
            "-fx-background-color: transparent; -fx-text-fill: #8A8FA3; -fx-background-radius: 10; " +
            "-fx-padding: 12 16; -fx-alignment: CENTER-LEFT; -fx-cursor: hand;";

    @FXML
    public void initialize() {
        em = JpaUtil.getEntityManager();
        eventRepo = new EventRepository(em, Event.class);
        sessionRepo = new SessionRepository(em, Session.class);
        teamRepo = new TeamRepository(em, Team.class);
        trackRepo = new TrackRepository(em, Track.class);
        juryRepo = new JuryRepository(em, Jury.class);
        participantRepo = new ParticipantRepository(em, Participant.class);
        visitorRepo = new VisitorRepository(em, Visitor.class);
        memberRepo = new MemberRepository(em, Member.class);
        scoreRepo = new ScoreRepository(em, Score.class);
        submissionRepo = new SubmissionRepository(em, Submission.class);

        User user = SessionContext.getCurrentUser();
        userNameLabel.setText(user.getName() + " " + user.getLastname());
        userRoleLabel.setText(user.getRole().toString());
        userEmailLabel.setText(user.getEmail());

        if (SessionContext.isUser()) {
            btnScores.setVisible(false);
            btnScores.setManaged(false);
        }

        showEvents();
    }

    // ─── EVENTS ──────────────────────────────────────────────────────────────

    @FXML public void showEvents() {
        setActiveButton(btnEvents);
        contentTitle.setText("Events");
        listContainer.getChildren().clear();
        hideDetail();
        for (Event event : eventRepo.findAllOrderedByStartDate()) {
            listContainer.getChildren().add(createEventCard(event));
        }
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox(6);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(46,49,66,0.06), 8, 0, 0, 2); " +
                "-fx-padding: 16; -fx-cursor: hand;");

        Label name = new Label(event.getName());
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");

        String loc = event.getLocation() != null ? event.getLocation().getLocationName() : "–";
        Label location = new Label("📍 " + loc);
        location.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3;");

        String start = event.getStartDate() != null ? event.getStartDate().format(DATE_FMT) : "–";
        String end = event.getEndDate() != null ? event.getEndDate().format(DATE_FMT) : "–";
        Label dates = new Label("📅 " + start + " – " + end);
        dates.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3;");

        Label status = new Label(event.getStatus() != null ? event.getStatus().toString() : "");
        status.setStyle("-fx-background-color: #EEF0FF; -fx-text-fill: #5B6EF5; " +
                "-fx-background-radius: 6; -fx-padding: 2 8; -fx-font-size: 11px; -fx-font-weight: bold;");

        User currentUser = SessionContext.getCurrentUser();
        card.getChildren().addAll(name, location, dates, status);
        if (!SessionContext.isJury()) {
            boolean registered = isRegisteredForEvent(currentUser, event);
            Button regBtn = makeRegBtn(registered ? "✓ Angemeldet" : "＋ Registrieren", registered);
            wireEventBtn(regBtn, event, currentUser, true);
            card.getChildren().add(regBtn);
        }
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #F0F2FF; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(91,110,245,0.15), 12, 0, 0, 4); -fx-padding: 16; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(46,49,66,0.06), 8, 0, 0, 2); -fx-padding: 16; -fx-cursor: hand;"));
        card.setOnMouseClicked(e -> showEventDetail(event));
        return card;
    }

    // ─── MEINE SESSIONS ──────────────────────────────────────────────────────

    @FXML public void showSessions() {
        setActiveButton(btnSessions);
        listContainer.getChildren().clear();
        hideDetail();

        User currentUser = SessionContext.getCurrentUser();

        if (SessionContext.isJury()) {
            // JURY: show tracks where assigned as jury
            contentTitle.setText("Meine Tracks");
            List<Jury> myJuries = juryRepo.findAll().stream()
                    .filter(j -> j.getUser() != null && j.getUser().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());

            if (myJuries.isEmpty()) {
                Label empty = new Label("Du wurdest noch keinem Track zugewiesen.");
                empty.setStyle("-fx-text-fill: #8A8FA3; -fx-font-size: 13px;");
                listContainer.getChildren().add(empty);
                return;
            }

            for (Jury jury : myJuries) {
                if (jury.getTrack() != null) {
                    VBox card = createJuryTrackCard(jury);
                    card.setOnMouseClicked(e -> showJuryTrackDetail(jury));
                    card.setOnMouseEntered(ev -> card.setStyle("-fx-background-color: #F0F2FF; -fx-background-radius: 12; " +
                            "-fx-effect: dropshadow(gaussian, rgba(91,110,245,0.15), 12, 0, 0, 4); -fx-padding: 16; -fx-cursor: hand;"));
                    card.setOnMouseExited(ev -> card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                            "-fx-effect: dropshadow(gaussian, rgba(46,49,66,0.06), 8, 0, 0, 2); -fx-padding: 16; -fx-cursor: hand;"));
                    listContainer.getChildren().add(card);
                }
            }
        } else {
            // USER: show sessions registered for
            contentTitle.setText("Meine Sessions");
            List<Visitor> visitors = visitorRepo.findAll().stream()
                    .filter(v -> v.getUser() != null && v.getUser().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());

            if (visitors.isEmpty()) {
                Label empty = new Label("Du hast dich noch für keine Session angemeldet.\nGehe zu Events und melde dich für eine Session an.");
                empty.setStyle("-fx-text-fill: #8A8FA3; -fx-font-size: 13px;");
                empty.setWrapText(true);
                listContainer.getChildren().add(empty);
                return;
            }

            for (Visitor v : visitors) {
                if (v.getSession() != null) {
                    VBox card = createSessionCard(v.getSession(), true, false);
                    card.setOnMouseClicked(e -> showSessionDetail(v.getSession()));
                    listContainer.getChildren().add(card);
                }
            }
        }
    }

    // ─── MEINE TEAMS ─────────────────────────────────────────────────────────

    @FXML public void showTeams() {
        setActiveButton(btnTeams);
        listContainer.getChildren().clear();
        hideDetail();

        User currentUser = SessionContext.getCurrentUser();

        if (SessionContext.isJury()) {
            // JURY: show teams from tracks they evaluate
            contentTitle.setText("Teams bewerten");
            List<Long> myTrackIds = juryRepo.findAll().stream()
                    .filter(j -> j.getUser() != null && j.getUser().getId().equals(currentUser.getId()))
                    .filter(j -> j.getTrack() != null)
                    .map(j -> j.getTrack().getId())
                    .collect(Collectors.toList());

            List<Team> teamsToEvaluate = teamRepo.findAll().stream()
                    .filter(t -> t.getTrack() != null && myTrackIds.contains(t.getTrack().getId()))
                    .collect(Collectors.toList());

            if (teamsToEvaluate.isEmpty()) {
                Label empty = new Label("Keine Teams in deinen Tracks gefunden.");
                empty.setStyle("-fx-text-fill: #8A8FA3; -fx-font-size: 13px;");
                listContainer.getChildren().add(empty);
                return;
            }

            for (Team team : teamsToEvaluate) {
                VBox card = createTeamCard(team);
                card.setOnMouseClicked(e -> {
                    Jury juryForTeam = juryRepo.findAll().stream()
                            .filter(j -> j.getUser() != null && j.getUser().getId().equals(currentUser.getId())
                                    && j.getTrack() != null && team.getTrack() != null
                                    && j.getTrack().getId().equals(team.getTrack().getId()))
                            .findFirst().orElse(null);
                    showJuryTeamDetail(team, juryForTeam);
                });
                card.setOnMouseEntered(ev -> card.setStyle("-fx-background-color: #F0F2FF; -fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(91,110,245,0.15), 12, 0, 0, 4); -fx-padding: 16; -fx-cursor: hand;"));
                card.setOnMouseExited(ev -> card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(46,49,66,0.06), 8, 0, 0, 2); -fx-padding: 16; -fx-cursor: hand;"));
                listContainer.getChildren().add(card);
            }
        } else {
            // USER: own teams only
            contentTitle.setText("Meine Teams");
            List<Team> myTeams = memberRepo.findAll().stream()
                    .filter(m -> m.getUser() != null && m.getUser().getId().equals(currentUser.getId()))
                    .map(Member::getTeam)
                    .filter(t -> t != null)
                    .distinct()
                    .collect(Collectors.toList());

            if (myTeams.isEmpty()) {
                Label empty = new Label("Du bist in keinem Team eingetragen.");
                empty.setStyle("-fx-text-fill: #8A8FA3; -fx-font-size: 13px;");
                empty.setWrapText(true);
                listContainer.getChildren().add(empty);
                return;
            }

            for (Team team : myTeams) {
                VBox card = createTeamCard(team);
                card.setOnMouseClicked(e -> showTeamDetail(team));
                card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #F0F2FF; -fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(91,110,245,0.15), 12, 0, 0, 4); -fx-padding: 16; -fx-cursor: hand;"));
                card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(46,49,66,0.06), 8, 0, 0, 2); -fx-padding: 16; -fx-cursor: hand;"));
                listContainer.getChildren().add(card);
            }
        }
    }

    // ─── SCORES ──────────────────────────────────────────────────────────────

    private void showTeamDetail(Team team) {
        detailContent.getChildren().clear();
        detailPanel.setVisible(true);
        detailPanel.setManaged(true);

        // Breadcrumb: Event / Track / Team
        String eventName = team.getTrack() != null && team.getTrack().getEvent() != null
                ? team.getTrack().getEvent().getName() : "–";
        String trackName = team.getTrack() != null ? team.getTrack().getName() : "–";
        Label breadcrumb = new Label(eventName + "  /  " + trackName + "  /  " + team.getName());
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3;");

        Label title = new Label(team.getName());
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");

        Label scoreLbl = new Label("Score: " + team.getScoreValue());
        scoreLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3; -fx-padding: 0 0 8 0;");

        detailContent.getChildren().addAll(breadcrumb, title, scoreLbl, new Separator());

        // Members
        addSectionTitle(detailContent, "Teammitglieder");
        List<Member> members = memberRepo.findByTeamId(team.getId());
        if (members.isEmpty()) {
            Label empty = new Label("Keine Mitglieder eingetragen.");
            empty.setStyle("-fx-text-fill: #8A8FA3; -fx-font-size: 12px;");
            detailContent.getChildren().add(empty);
        } else {
            VBox membersBox = new VBox(10);
            membersBox.setStyle("-fx-padding: 4 0 0 0;");
            for (Member member : members) {
                VBox row = new VBox(4);
                row.setStyle("-fx-background-color: #F4F6FB; -fx-background-radius: 10; -fx-padding: 12 16;");

                String name = member.getUser() != null
                        ? member.getUser().getName() + " " + member.getUser().getLastname() : "–";
                Label nameLbl = new Label(name);
                nameLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");
                row.getChildren().add(nameLbl);

                if (member.getTeamRole() != null && !member.getTeamRole().isBlank()) {
                    Label roleLbl = new Label("Stack / Rolle:  " + member.getTeamRole());
                    roleLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #5B6EF5;");
                    row.getChildren().add(roleLbl);
                }

                if (member.getUser() != null && member.getUser().getEmail() != null) {
                    Label emailLbl = new Label(member.getUser().getEmail());
                    emailLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3;");
                    row.getChildren().add(emailLbl);
                }

                membersBox.getChildren().add(row);
            }
            detailContent.getChildren().add(membersBox);
        }

        detailContent.getChildren().add(new Separator());
        addSectionTitle(detailContent, "Projekt einreichen");
        
        // Check existing submission
        List<Submission> submissions = submissionRepo.findAll().stream()
                .filter(s -> s instanceof SubmissionTeam && 
                        ((SubmissionTeam)s).getTeam() != null &&
                        ((SubmissionTeam)s).getTeam().getId().equals(team.getId()))
                .collect(java.util.stream.Collectors.toList());
        
        SubmissionTeam existing = submissions.isEmpty() ? null : (SubmissionTeam) submissions.get(0);

        Label titleFieldLbl = new Label("Projekttitel");
        titleFieldLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3; -fx-font-weight: bold;");
        TextField titleField = new TextField(existing != null ? existing.getTitel() : "");
        titleField.setPromptText("z.B. SmartCity App");
        titleField.setStyle("-fx-background-color: #F4F6FB; -fx-background-radius: 8; -fx-border-color: #E4E7F0; -fx-border-radius: 8; -fx-padding: 8;");

        Label gitLbl = new Label("GitHub URL");
        gitLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3; -fx-font-weight: bold;");
        TextField gitField = new TextField(existing != null && existing.getGitUrl() != null ? existing.getGitUrl() : "");
        gitField.setPromptText("https://github.com/...");
        gitField.setStyle("-fx-background-color: #F4F6FB; -fx-background-radius: 8; -fx-border-color: #E4E7F0; -fx-border-radius: 8; -fx-padding: 8;");

        Label demoLbl = new Label("Demo URL (optional)");
        demoLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3; -fx-font-weight: bold;");
        TextField demoField = new TextField(existing != null && existing.getDemoUrl() != null ? existing.getDemoUrl() : "");
        demoField.setPromptText("https://...");
        demoField.setStyle("-fx-background-color: #F4F6FB; -fx-background-radius: 8; -fx-border-color: #E4E7F0; -fx-border-radius: 8; -fx-padding: 8;");

        Label statusLbl = new Label("");
        statusLbl.setWrapText(true);

        Button submitBtn = new Button(existing != null ? "Aktualisieren" : "Projekt einreichen");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        submitBtn.setStyle("-fx-background-color: #5B6EF5; -fx-text-fill: white; -fx-background-radius: 10; " +
                "-fx-padding: 12 20; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand;");
        
        submitBtn.setOnAction(e -> {
            String titel = titleField.getText().trim();
            String git = gitField.getText().trim();
            if (titel.isBlank()) {
                statusLbl.setStyle("-fx-text-fill: #E2574C; -fx-font-size: 12px;");
                statusLbl.setText("Bitte einen Projekttitel eingeben.");
                return;
            }
            try {
                if (existing != null) {
                    existing.setTitel(titel);
                    existing.setGitUrl(git.isBlank() ? null : git);
                    existing.setDemoUrl(demoField.getText().trim().isBlank() ? null : demoField.getText().trim());
                    submissionRepo.save(existing);
                } else {
                    SubmissionTeam sub = new SubmissionTeam(
                            titel, "", ParticipationType.TEAM,
                            git.isBlank() ? null : git,
                            demoField.getText().trim().isBlank() ? null : demoField.getText().trim(),
                            team
                    );
                    em.getTransaction().begin();
                    em.persist(sub);
                    em.getTransaction().commit();
                }
                statusLbl.setStyle("-fx-text-fill: #10B981; -fx-font-size: 12px;");
                statusLbl.setText("✓ Erfolgreich gespeichert!");
                submitBtn.setText("Aktualisieren");
            } catch (Exception ex) {
                statusLbl.setStyle("-fx-text-fill: #E2574C; -fx-font-size: 12px;");
                statusLbl.setText("Fehler: " + ex.getMessage());
            }
        });

        detailContent.getChildren().addAll(
                titleFieldLbl, titleField,
                gitLbl, gitField,
                demoLbl, demoField,
                submitBtn, statusLbl
        );
    }

    @FXML public void showScores() {
        setActiveButton(btnScores);
        contentTitle.setText("Bewertungen");
        listContainer.getChildren().clear();
        hideDetail();
        User currentUser = SessionContext.getCurrentUser();
        for (Jury jury : juryRepo.findAll()) {
            if (jury.getUser() != null && jury.getUser().getId().equals(currentUser.getId())) {
                listContainer.getChildren().add(createJuryCard(jury));
            }
        }
        if (listContainer.getChildren().isEmpty()) {
            Label empty = new Label("Keine Bewertungsaufgaben gefunden.");
            empty.setStyle("-fx-text-fill: #8A8FA3; -fx-font-size: 13px;");
            listContainer.getChildren().add(empty);
        }
    }

    // ─── DETAIL: EVENT ───────────────────────────────────────────────────────

    private void showEventDetail(Event event) {
        detailContent.getChildren().clear();
        detailPanel.setVisible(true);
        detailPanel.setManaged(true);

        // Title
        Label title = new Label(event.getName());
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");
        title.setWrapText(true);
        Label subtitle = new Label(event.getStatus() != null ? event.getStatus().toString() : "");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #5B6EF5; -fx-font-weight: bold; -fx-padding: 4 0 12 0;");
        detailContent.getChildren().addAll(title, subtitle, new Separator());

        // Info rows
        VBox infoBox = new VBox(12);
        infoBox.setStyle("-fx-padding: 16 0 0 0;");

        String loc = event.getLocation() != null ?
                event.getLocation().getLocationName() +
                (event.getLocation().getAddress() != null ? ", " + event.getLocation().getAddress() : "") +
                (event.getLocation().getStadt() != null ? ", " + event.getLocation().getStadt() : "") : "–";
        infoBox.getChildren().add(infoRow("📍", "Ort", loc));

        String start = event.getStartDate() != null ? event.getStartDate().format(DATE_FMT) : "–";
        String end = event.getEndDate() != null ? event.getEndDate().format(DATE_FMT) : "–";
        infoBox.getChildren().add(infoRow("📅", "Zeitraum", start + " – " + end));
        infoBox.getChildren().add(infoRow("🏷️", "Status", event.getStatus() != null ? event.getStatus().toString() : "–"));
        infoBox.getChildren().add(infoRow("📝", "Beschreibung",
                event.getDescription() != null ? event.getDescription() : "Keine Beschreibung."));

        detailContent.getChildren().add(infoBox);
        detailContent.getChildren().add(new Separator());

        // Tracks — accordion style
        addSectionTitle(detailContent, "Tracks");
        VBox tracksBox = new VBox(8);
        for (Track track : trackRepo.findAll()) {
            if (track.getEvent() != null && track.getEvent().getId().equals(event.getId())) {
                // Header row (always visible, clickable)
                VBox tCard = new VBox(0);
                tCard.setStyle("-fx-background-color: #F4F6FB; -fx-background-radius: 10; -fx-cursor: hand;");

                HBox tHeader = new HBox(10);
                tHeader.setStyle("-fx-padding: 12 16; -fx-alignment: CENTER-LEFT;");
                Label arrow = new Label("▶");
                arrow.setStyle("-fx-font-size: 11px; -fx-text-fill: #5B6EF5;");
                Label tName = new Label(track.getName());
                tName.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                if (track.getDeadlineDate() != null) {
                    Label dl = new Label("Deadline: " + track.getDeadlineDate().format(DATE_FMT));
                    dl.setStyle("-fx-font-size: 11px; -fx-text-fill: #E2574C;");
                    tHeader.getChildren().addAll(arrow, tName, spacer, dl);
                } else {
                    tHeader.getChildren().addAll(arrow, tName);
                }

                // Preview (always visible) — 2 lines max
                VBox tPreview = new VBox(4);
                tPreview.setStyle("-fx-padding: 0 16 10 40; -fx-background-color: #F4F6FB;");

                // Full body (hidden)
                VBox tBody = new VBox(8);
                tBody.setStyle("-fx-padding: 0 16 14 40; -fx-background-color: #F4F6FB;");
                tBody.setVisible(false);
                tBody.setManaged(false);

                if (track.getDescription() != null && !track.getDescription().isBlank()) {
                    String desc = track.getDescription();
                    // Preview: first 120 chars
                    String preview = desc.length() > 120 ? desc.substring(0, 120) + "..." : desc;
                    Label tPreviewLbl = new Label(preview);
                    tPreviewLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3;");
                    tPreviewLbl.setWrapText(true);
                    tPreview.getChildren().add(tPreviewLbl);

                    if (desc.length() > 120) {
                        Label tFullLbl = new Label(desc);
                        tFullLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #5A5F72;");
                        tFullLbl.setWrapText(true);
                        tBody.getChildren().add(tFullLbl);

                        Label moreLink = new Label("Mehr anzeigen...");
                        moreLink.setStyle("-fx-text-fill: #5B6EF5; -fx-font-size: 12px; -fx-cursor: hand;");
                        tPreview.getChildren().add(moreLink);
                    } else {
                        Label tFullLbl = new Label(desc);
                        tFullLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #5A5F72;");
                        tFullLbl.setWrapText(true);
                        tBody.getChildren().add(tFullLbl);
                    }
                }

                // Toggle on click
                tHeader.setOnMouseClicked(e -> {
                    boolean expanded = tBody.isVisible();
                    tBody.setVisible(!expanded);
                    tBody.setManaged(!expanded);
                    tPreview.setVisible(expanded);
                    tPreview.setManaged(expanded);
                    arrow.setText(expanded ? "▶" : "▼");
                });

                tCard.getChildren().addAll(tHeader, tPreview, tBody);
                tracksBox.getChildren().add(tCard);
            }
        }
        if (tracksBox.getChildren().isEmpty()) {
            tracksBox.getChildren().add(new Label("Keine Tracks vorhanden."));
        }
        detailContent.getChildren().add(tracksBox);

        // Sessions
        List<Session> sessions = sessionRepo.findAll().stream()
                .filter(s -> s.getEvent() != null && s.getEvent().getId().equals(event.getId()))
                .collect(Collectors.toList());

        if (!sessions.isEmpty()) {
            detailContent.getChildren().add(new Separator());
            addSectionTitle(detailContent, "Sessions");
            VBox sessionsBox = new VBox(8);
            User currentUser = SessionContext.getCurrentUser();
            for (Session session : sessions) {
                boolean alreadyVisitor = visitorRepo.findByUserAndSession(
                        currentUser.getId(), session.getId()).isPresent();
                VBox sCard = createSessionCard(session, alreadyVisitor, true);
                // wire session buttons
                List<javafx.scene.Node> btns = sCard.getChildren().stream()
                        .filter(n -> n instanceof Button)
                        .collect(Collectors.toList());
                if (!btns.isEmpty()) {
                    Button sBtn = (Button) btns.get(0);
                    wireSessionBtn(sBtn, session, currentUser, alreadyVisitor);
                }
                sCard.setOnMouseClicked(e -> showSessionDetail(session));
                sessionsBox.getChildren().add(sCard);
            }
            detailContent.getChildren().add(sessionsBox);
        }

        // Jury
        detailContent.getChildren().add(new Separator());
        addSectionTitle(detailContent, "Jury");
        VBox juryBox = new VBox(6);
        for (Jury jury : juryRepo.findAll()) {
            if (jury.getTrack() != null && jury.getTrack().getEvent() != null &&
                    jury.getTrack().getEvent().getId().equals(event.getId())) {
                String juryName = jury.getUser() != null ?
                        jury.getUser().getName() + " " + jury.getUser().getLastname() : "–";
                String area = jury.getProfArea() != null ? " · " + jury.getProfArea() : "";
                Label lbl = new Label("👤 " + juryName + area);
                lbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #2E3142;");
                juryBox.getChildren().add(lbl);
            }
        }
        if (juryBox.getChildren().isEmpty()) {
            juryBox.getChildren().add(new Label("Noch kein Jury zugewiesen."));
        }
        detailContent.getChildren().add(juryBox);

        // Register / Unregister button (large, synced) — not for JURY
        if (!SessionContext.isJury()) {
            detailContent.getChildren().add(new Separator());
            User currentUser = SessionContext.getCurrentUser();
            boolean registered = isRegisteredForEvent(currentUser, event);
            Button regBtn = new Button(registered ? "✓ Angemeldet" : "An diesem Event teilnehmen");
            regBtn.setMaxWidth(Double.MAX_VALUE);
            applyBtnStyleLarge(regBtn, registered);
            wireEventBtn(regBtn, event, currentUser, false);
            detailContent.getChildren().add(regBtn);
        }
    }

    // ─── DETAIL: SESSION ─────────────────────────────────────────────────────

    private void showSessionDetail(Session session) {
        detailContent.getChildren().clear();
        detailPanel.setVisible(true);
        detailPanel.setManaged(true);

        // Title + type
        HBox titleRow = new HBox(10);
        Label title = new Label(session.getTitel() != null ? session.getTitel() : "Session");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");
        title.setWrapText(true);
        HBox.setHgrow(title, Priority.ALWAYS);
        if (session.getSessionType() != null) {
            Label type = new Label(session.getSessionType().toString());
            type.setStyle("-fx-background-color: #EEF0FF; -fx-text-fill: #5B6EF5; " +
                    "-fx-background-radius: 6; -fx-padding: 4 10; -fx-font-size: 11px; -fx-font-weight: bold;");
            titleRow.getChildren().addAll(title, type);
        } else {
            titleRow.getChildren().add(title);
        }
        detailContent.getChildren().add(titleRow);

        if (session.getEvent() != null) {
            Label eventLbl = new Label("🎯 " + session.getEvent().getName());
            eventLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #5B6EF5; -fx-padding: 4 0 12 0;");
            detailContent.getChildren().add(eventLbl);
        }

        detailContent.getChildren().add(new Separator());

        VBox infoBox = new VBox(12);
        infoBox.setStyle("-fx-padding: 16 0 0 0;");

        // Description
        if (session.getDescription() != null && !session.getDescription().isBlank()) {
            infoBox.getChildren().add(infoRow("📝", "Beschreibung", session.getDescription()));
        }

        // Time
        String start = session.getStartDate() != null ?
                session.getStartDate().format(DATE_FMT) + "  " + session.getStartDate().format(TIME_FMT) : "–";
        String end = session.getEndDate() != null ? session.getEndDate().format(TIME_FMT) : "–";
        infoBox.getChildren().add(infoRow("📅", "Zeitraum", start + " – " + end + " Uhr"));

        // Room
        if (session.getRoom() != null) {
            infoBox.getChildren().add(infoRow("🚪", "Raum", session.getRoom()));
        }

        // Capacity
        infoBox.getChildren().add(infoRow("👥", "Kapazität", String.valueOf(session.getCapacity()) + " Plätze"));

        detailContent.getChildren().add(infoBox);
        detailContent.getChildren().add(new Separator());

        // Register button
        User currentUser = SessionContext.getCurrentUser();
        boolean alreadyVisitor = visitorRepo.findByUserAndSession(
                currentUser.getId(), session.getId()).isPresent();
        Button sBtn = new Button(alreadyVisitor ? "✓ Angemeldet" : "Für diese Session anmelden");
        sBtn.setMaxWidth(Double.MAX_VALUE);
        applyBtnStyleLarge(sBtn, alreadyVisitor);
        wireSessionBtn(sBtn, session, currentUser, alreadyVisitor);
        detailContent.getChildren().add(sBtn);
    }

    // ─── CARD BUILDERS ───────────────────────────────────────────────────────

    private VBox createSessionCard(Session session, boolean registered, boolean showRegBtn) {
        VBox card = new VBox(6);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(46,49,66,0.06), 8, 0, 0, 2); -fx-padding: 16; -fx-cursor: hand;");

        HBox header = new HBox(10);
        Label name = new Label(session.getTitel() != null ? session.getTitel() : "Session");
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");
        name.setWrapText(true);
        HBox.setHgrow(name, Priority.ALWAYS);
        if (session.getSessionType() != null) {
            Label type = new Label(session.getSessionType().toString());
            type.setStyle("-fx-background-color: #EEF0FF; -fx-text-fill: #5B6EF5; " +
                    "-fx-background-radius: 6; -fx-padding: 3 8; -fx-font-size: 10px; -fx-font-weight: bold;");
            header.getChildren().addAll(name, type);
        } else {
            header.getChildren().add(name);
        }
        card.getChildren().add(header);

        if (session.getDescription() != null && !session.getDescription().isBlank()) {
            Label desc = new Label(session.getDescription());
            desc.setStyle("-fx-font-size: 12px; -fx-text-fill: #5A5F72;");
            desc.setWrapText(true);
            card.getChildren().add(desc);
        }

        String start = session.getStartDate() != null ?
                session.getStartDate().format(DATE_FMT) + "  " + session.getStartDate().format(TIME_FMT) : "–";
        String end = session.getEndDate() != null ? session.getEndDate().format(TIME_FMT) : "–";
        Label date = new Label("📅  " + start + " – " + end + " Uhr");
        date.setStyle("-fx-font-size: 12px; -fx-text-fill: #5B6EF5;");
        card.getChildren().add(date);

        HBox meta = new HBox(16);
        if (session.getRoom() != null) {
            Label room = new Label("🚪  " + session.getRoom());
            room.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3;");
            meta.getChildren().add(room);
        }
        Label cap = new Label("👥  " + session.getCapacity() + " Plätze");
        cap.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3;");
        meta.getChildren().add(cap);
        card.getChildren().add(meta);

        if (showRegBtn) {
            Button btn = makeRegBtn(registered ? "✓ Angemeldet" : "＋ Anmelden", registered);
            card.getChildren().add(btn);
        } else if (registered) {
            Label badge = new Label("✓ Angemeldet");
            badge.setStyle("-fx-background-color: #ECFDF5; -fx-text-fill: #10B981; " +
                    "-fx-background-radius: 6; -fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold;");
            card.getChildren().add(badge);
        }

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #F0F2FF; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(91,110,245,0.15), 12, 0, 0, 4); -fx-padding: 16; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(46,49,66,0.06), 8, 0, 0, 2); -fx-padding: 16; -fx-cursor: hand;"));

        return card;
    }

    private VBox createTeamCard(Team team) {
        VBox card = new VBox(6);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(46,49,66,0.06), 8, 0, 0, 2); -fx-padding: 16;");
        Label name = new Label("👥 " + team.getName());
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");
        String track = team.getTrack() != null ? "🎯 Track: " + team.getTrack().getName() : "";
        Label trackLabel = new Label(track);
        trackLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3;");
        String eventName = team.getTrack() != null && team.getTrack().getEvent() != null
                ? "🎪 " + team.getTrack().getEvent().getName() : "";
        Label eventLabel = new Label(eventName);
        eventLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3;");
        Label scoreLabel = new Label("⭐ Score: " + team.getScoreValue());
        scoreLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3;");
        card.getChildren().addAll(name, trackLabel, eventLabel, scoreLabel);
        return card;
    }

    private VBox createJuryCard(Jury jury) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(46,49,66,0.06), 8, 0, 0, 2); -fx-padding: 16;");

        Label track = new Label("🎯 " + (jury.getTrack() != null ? jury.getTrack().getName() : "–"));
        track.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");
        card.getChildren().add(track);

        if (jury.getProfArea() != null && !jury.getProfArea().isBlank()) {
            Label area = new Label("💼 " + jury.getProfArea());
            area.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3;");
            card.getChildren().add(area);
        }

        // Собираем все оценки этого jury
        List<Score> myScores = scoreRepo.findAll().stream()
                .filter(s -> s.getJury() != null && s.getJury().getId().equals(jury.getId()))
                .collect(Collectors.toList());

        if (myScores.isEmpty()) {
            Label noScore = new Label("⏳ Noch keine Bewertungen abgegeben");
            noScore.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3; -fx-padding: 4 0 0 0;");
            card.getChildren().add(noScore);
        } else {
            javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
            sep.setStyle("-fx-padding: 4 0;");
            card.getChildren().add(sep);

            Label scoresTitle = new Label("Abgegebene Bewertungen:");
            scoresTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #8A8FA3;");
            card.getChildren().add(scoresTitle);

            VBox scoresList = new VBox(6);
            for (Score s : myScores) {
                HBox row = new HBox(10);
                row.setStyle("-fx-background-color: #F4F6FB; -fx-background-radius: 8; -fx-padding: 8 12; -fx-alignment: CENTER-LEFT;");

                // Название команды через submission → submissionTeam → team
                String teamName = "–";
                if (s.getSubmission() instanceof SubmissionTeam st && st.getTeam() != null) {
                    teamName = st.getTeam().getName();
                } else if (s.getSubmission() != null && s.getSubmission().getTitel() != null) {
                    teamName = s.getSubmission().getTitel();
                }
                Label teamLbl = new Label("👥 " + teamName);
                teamLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");
                HBox.setHgrow(teamLbl, Priority.ALWAYS);

                // Критерий
                String criteriaText = s.getCriteria() != null ? s.getCriteria() : "";
                Label criteriaLbl = new Label(criteriaText);
                criteriaLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3;");

                // Оценка — цветная плашка
                int val = s.getScoreValue() != null ? s.getScoreValue() : 0;
                String badgeColor = val >= 8 ? "#ECFDF5" : val >= 5 ? "#EEF0FF" : "#FEF2F2";
                String textColor  = val >= 8 ? "#10B981" : val >= 5 ? "#5B6EF5" : "#E2574C";
                Label scoreBadge = new Label("★ " + val + " / 10");
                scoreBadge.setStyle("-fx-background-color: " + badgeColor + "; -fx-text-fill: " + textColor + "; " +
                        "-fx-background-radius: 6; -fx-padding: 3 10; -fx-font-size: 12px; -fx-font-weight: bold;");

                row.getChildren().addAll(teamLbl, criteriaLbl, scoreBadge);
                scoresList.getChildren().add(row);

                // Комментарий (если есть)
                if (s.getComment() != null && !s.getComment().isBlank()) {
                    Label commentLbl = new Label("💬 " + s.getComment());
                    commentLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3; -fx-padding: 0 0 0 12;");
                    commentLbl.setWrapText(true);
                    scoresList.getChildren().add(commentLbl);
                }
            }
            card.getChildren().add(scoresList);

            // Средняя оценка
            double avg = myScores.stream()
                    .filter(s -> s.getScoreValue() != null)
                    .mapToInt(Score::getScoreValue)
                    .average().orElse(0);
            Label avgLbl = new Label(String.format("Ø Durchschnitt: %.1f / 10", avg));
            avgLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #5B6EF5; -fx-padding: 4 0 0 0;");
            card.getChildren().add(avgLbl);
        }

        return card;
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private HBox infoRow(String icon, String labelText, String value) {
        HBox row = new HBox(12);
        Label ico = new Label(icon);
        ico.setStyle("-fx-font-size: 16px;");
        VBox vb = new VBox(2);
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3; -fx-font-weight: bold;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E3142;");
        val.setWrapText(true);
        vb.getChildren().addAll(lbl, val);
        HBox.setHgrow(vb, Priority.ALWAYS);
        row.getChildren().addAll(ico, vb);
        return row;
    }

    private void addSectionTitle(VBox parent, String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #8A8FA3; -fx-padding: 12 0 6 0;");
        parent.getChildren().add(lbl);
    }

    /** blue = unregistered, green = registered, red = hover to cancel */
    private Button makeRegBtn(String text, boolean registered) {
        Button btn = new Button(text);
        applyBtnStyle(btn, registered);
        return btn;
    }

    private void applyBtnStyle(Button btn, boolean registered) {
        if (registered) {
            btn.setStyle("-fx-background-color: #FEF2F2; -fx-text-fill: #E2574C; " +
                    "-fx-background-radius: 8; -fx-padding: 5 14; -fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand;");
            btn.setDisable(false);
        } else {
            btn.setStyle("-fx-background-color: #5B6EF5; -fx-text-fill: white; " +
                    "-fx-background-radius: 8; -fx-padding: 5 14; -fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand;");
            btn.setDisable(false);
        }
    }

    private void applyBtnStyleLarge(Button btn, boolean registered) {
        if (registered) {
            btn.setStyle("-fx-background-color: #FEF2F2; -fx-text-fill: #E2574C; " +
                    "-fx-background-radius: 10; -fx-padding: 12 20; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: #5B6EF5; -fx-text-fill: white; " +
                    "-fx-background-radius: 10; -fx-padding: 12 20; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand;");
        }
    }

    private boolean isRegisteredForEvent(User user, Event event) {
        return participantRepo.findByUserId(user.getId()).stream()
                .anyMatch(p -> p.getTrack() != null && p.getTrack().getEvent() != null &&
                        p.getTrack().getEvent().getId().equals(event.getId()));
    }

    private void registerForEvent(Event event, Button btn) {
        try {
            User currentUser = SessionContext.getCurrentUser();
            Track firstTrack = trackRepo.findAll().stream()
                    .filter(t -> t.getEvent() != null && t.getEvent().getId().equals(event.getId()))
                    .findFirst().orElse(null);
            if (firstTrack == null) { btn.setText("⚠️ Kein Track"); return; }
            participantRepo.save(new Participant(currentUser, firstTrack, null));
            btn.setText("✓ Angemeldet");
            btn.setStyle("-fx-background-color: #E2574C; -fx-text-fill: #10B981; -fx-background-radius: 8; -fx-padding: 5 14; -fx-font-size: 12px; -fx-cursor: default;");
            btn.setDisable(true);
        } catch (Exception e) { btn.setText("⚠️ Fehler"); }
    }

    /** Wire a session button to toggle between Anmelden/Abmelden */
    private void wireSessionBtn(Button btn, Session session, User user, boolean currentlyRegistered) {
        if (currentlyRegistered) {
            btn.setText("Abmelden");
            applyBtnStyle(btn, true);
            btn.setOnAction(e -> {
                visitorRepo.findByUserAndSession(user.getId(), session.getId())
                        .ifPresent(visitorRepo::delete);
                btn.setText("Anmelden");
                applyBtnStyle(btn, false);
                wireSessionBtn(btn, session, user, false);
            });
        } else {
            btn.setText("Anmelden");
            applyBtnStyle(btn, false);
            btn.setOnAction(e -> {
                try {
                    visitorRepo.save(new Visitor(user, session));
                    btn.setText("Abmelden");
                    applyBtnStyle(btn, true);
                    wireSessionBtn(btn, session, user, true);
                } catch (Exception ex) { btn.setText("Fehler"); }
            });
        }
    }

    /** Wire an event button to toggle between Teilnehmen/Abmelden. isSmall = card button */
    private void wireEventBtn(Button btn, Event event, User user, boolean isSmall) {
        boolean reg = isRegisteredForEvent(user, event);
        if (reg) {
            btn.setText(isSmall ? "✓ Angemeldet" : "Abmelden");
            if (isSmall) applyBtnStyle(btn, true); else applyBtnStyleLarge(btn, true);
            btn.setOnAction(e -> {
                participantRepo.findByUserId(user.getId()).stream()
                        .filter(p -> p.getTrack() != null && p.getTrack().getEvent() != null &&
                                p.getTrack().getEvent().getId().equals(event.getId()))
                        .forEach(participantRepo::delete);
                btn.setText(isSmall ? "＋ Registrieren" : "An diesem Event teilnehmen");
                if (isSmall) applyBtnStyle(btn, false); else applyBtnStyleLarge(btn, false);
                wireEventBtn(btn, event, user, isSmall);
            });
        } else {
            btn.setText(isSmall ? "＋ Registrieren" : "An diesem Event teilnehmen");
            if (isSmall) applyBtnStyle(btn, false); else applyBtnStyleLarge(btn, false);
            btn.setOnAction(e -> {
                registerForEvent(event, btn);
                btn.setText(isSmall ? "✓ Angemeldet" : "Abmelden");
                if (isSmall) applyBtnStyle(btn, true); else applyBtnStyleLarge(btn, true);
                wireEventBtn(btn, event, user, isSmall);
                e.consume();
            });
        }
    }

    // ─── JURY TRACK CARD ─────────────────────────────────────────────────────

    private VBox createJuryTrackCard(Jury jury) {
        VBox card = new VBox(6);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(46,49,66,0.06), 8, 0, 0, 2); -fx-padding: 16; -fx-cursor: hand;");

        Track track = jury.getTrack();
        Label name = new Label(track.getName());
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");

        String eventName = track.getEvent() != null ? track.getEvent().getName() : "–";
        Label event = new Label(eventName);
        event.setStyle("-fx-font-size: 12px; -fx-text-fill: #5B6EF5;");

        String area = jury.getProfArea() != null ? "Fachgebiet: " + jury.getProfArea() : "";
        Label profArea = new Label(area);
        profArea.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3;");

        if (track.getDeadlineDate() != null) {
            Label dl = new Label("Deadline: " + track.getDeadlineDate().format(DATE_FMT));
            dl.setStyle("-fx-font-size: 11px; -fx-text-fill: #E2574C;");
            card.getChildren().addAll(name, event, profArea, dl);
        } else {
            card.getChildren().addAll(name, event, profArea);
        }
        return card;
    }

    private void showJuryTrackDetail(Jury jury) {
        detailContent.getChildren().clear();
        detailPanel.setVisible(true);
        detailPanel.setManaged(true);

        Track track = jury.getTrack();
        String eventName = track.getEvent() != null ? track.getEvent().getName() : "–";

        Label breadcrumb = new Label(eventName + "  /  " + track.getName());
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3;");
        Label title = new Label(track.getName());
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");

        detailContent.getChildren().addAll(breadcrumb, title, new Separator());

        if (track.getDescription() != null && !track.getDescription().isBlank()) {
            Label desc = new Label(track.getDescription());
            desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #5A5F72;");
            desc.setWrapText(true);
            detailContent.getChildren().add(desc);
        }

        if (track.getDeadlineDate() != null) {
            Label dl = new Label("Deadline: " + track.getDeadlineDate().format(DATE_FMT));
            dl.setStyle("-fx-font-size: 12px; -fx-text-fill: #E2574C;");
            detailContent.getChildren().add(dl);
        }

    }

    private void showJuryTeamDetail(Team team, Jury juryObj) {
        detailContent.getChildren().clear();
        detailPanel.setVisible(true);
        detailPanel.setManaged(true);

        String trackName = team.getTrack() != null ? team.getTrack().getName() : "–";
        String eventName = team.getTrack() != null && team.getTrack().getEvent() != null
                ? team.getTrack().getEvent().getName() : "–";

        Label breadcrumb = new Label(eventName + "  /  " + trackName + "  /  " + team.getName());
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3;");
        Label title = new Label(team.getName());
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");

        detailContent.getChildren().addAll(breadcrumb, title, new Separator());

        // Members (read-only)
        addSectionTitle(detailContent, "Teammitglieder");
        List<Member> members = memberRepo.findByTeamId(team.getId());
        if (members.isEmpty()) {
            detailContent.getChildren().add(new Label("Keine Mitglieder."));
        } else {
            VBox membersBox = new VBox(8);
            for (Member member : members) {
                VBox row = new VBox(4);
                row.setStyle("-fx-background-color: #F4F6FB; -fx-background-radius: 10; -fx-padding: 12 16;");
                String name = member.getUser() != null
                        ? member.getUser().getName() + " " + member.getUser().getLastname() : "–";
                Label nameLbl = new Label(name);
                nameLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");
                row.getChildren().add(nameLbl);
                if (member.getTeamRole() != null && !member.getTeamRole().isBlank()) {
                    Label roleLbl = new Label("Stack / Rolle:  " + member.getTeamRole());
                    roleLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #5B6EF5;");
                    row.getChildren().add(roleLbl);
                }
                membersBox.getChildren().add(row);
            }
            detailContent.getChildren().add(membersBox);
        }

        detailContent.getChildren().add(new Separator());

        // Submission (read-only)
        addSectionTitle(detailContent, "Eingereichtes Projekt");
        List<Submission> subs = submissionRepo.findAll().stream()
                .filter(s -> s instanceof SubmissionTeam &&
                        ((SubmissionTeam) s).getTeam() != null &&
                        ((SubmissionTeam) s).getTeam().getId().equals(team.getId()))
                .collect(Collectors.toList());

        if (subs.isEmpty()) {
            Label empty = new Label("Noch kein Projekt eingereicht.");
            empty.setStyle("-fx-text-fill: #8A8FA3; -fx-font-size: 12px;");
            detailContent.getChildren().add(empty);
        } else {
            SubmissionTeam sub = (SubmissionTeam) subs.get(0);
            VBox subBox = new VBox(8);
            subBox.setStyle("-fx-background-color: #F4F6FB; -fx-background-radius: 10; -fx-padding: 14 16;");

            Label subTitle = new Label(sub.getTitel() != null ? sub.getTitel() : "–");
            subTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");
            subBox.getChildren().add(subTitle);

            if (sub.getGitUrl() != null && !sub.getGitUrl().isBlank()) {
                Label git = new Label("GitHub: " + sub.getGitUrl());
                git.setStyle("-fx-font-size: 12px; -fx-text-fill: #5B6EF5;");
                subBox.getChildren().add(git);
            }
            if (sub.getDemoUrl() != null && !sub.getDemoUrl().isBlank()) {
                Label demo = new Label("Demo: " + sub.getDemoUrl());
                demo.setStyle("-fx-font-size: 12px; -fx-text-fill: #5B6EF5;");
                subBox.getChildren().add(demo);
            }
            detailContent.getChildren().add(subBox);
        }

        detailContent.getChildren().add(new Separator());

        // Score form
        addSectionTitle(detailContent, "Bewertung");

        // Check existing score from this jury for this team
        List<Score> existingScores = scoreRepo.findAll().stream()
                .filter(s -> s.getSubmission() != null && !subs.isEmpty() &&
                        s.getSubmission().getId().equals(subs.get(0).getId()) &&
                        s.getJury() != null && juryObj != null &&
                        s.getJury().getId().equals(juryObj.getId()))
                .collect(Collectors.toList());
        Score existingScore = existingScores.isEmpty() ? null : existingScores.get(0);

        Label criteriaLbl = new Label("Kriterien (z.B. Innovation, Design)");
        criteriaLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3; -fx-font-weight: bold;");
        javafx.scene.control.TextField criteriaField = new javafx.scene.control.TextField(
                existingScore != null && existingScore.getCriteria() != null ? existingScore.getCriteria() : "");
        criteriaField.setPromptText("z.B. Innovation");
        criteriaField.setStyle("-fx-background-color: #F4F6FB; -fx-background-radius: 8; -fx-border-color: #E4E7F0; -fx-border-radius: 8; -fx-padding: 8;");

        Label scoreLbl = new Label("Punktzahl (0 – 10)");
        scoreLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3; -fx-font-weight: bold;");
        javafx.scene.control.TextField scoreField = new javafx.scene.control.TextField(
                existingScore != null ? String.valueOf(existingScore.getScoreValue()) : "");
        scoreField.setPromptText("0 – 10");
        scoreField.setStyle("-fx-background-color: #F4F6FB; -fx-background-radius: 8; -fx-border-color: #E4E7F0; -fx-border-radius: 8; -fx-padding: 8;");

        Label commentLbl = new Label("Kommentar");
        commentLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8FA3; -fx-font-weight: bold;");
        javafx.scene.control.TextArea commentArea = new javafx.scene.control.TextArea(
                existingScore != null && existingScore.getComment() != null ? existingScore.getComment() : "");
        commentArea.setPromptText("Kommentar...");
        commentArea.setPrefRowCount(3);
        commentArea.setWrapText(true);
        commentArea.setStyle("-fx-background-color: #F4F6FB; -fx-background-radius: 8; -fx-border-color: #E4E7F0; -fx-border-radius: 8;");

        Label statusLbl = new Label("");
        statusLbl.setWrapText(true);

        Button scoreBtn = new Button(existingScore != null ? "Bewertung aktualisieren" : "Bewertung abgeben");
        scoreBtn.setMaxWidth(Double.MAX_VALUE);
        scoreBtn.setStyle("-fx-background-color: #5B6EF5; -fx-text-fill: white; -fx-background-radius: 10; " +
                "-fx-padding: 12 20; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand;");

        final Score[] scoreRef = {existingScore};
        scoreBtn.setOnAction(e -> {
            String criteria = criteriaField.getText().trim();
            String scoreText = scoreField.getText().trim();
            if (criteria.isBlank() || scoreText.isBlank()) {
                statusLbl.setStyle("-fx-text-fill: #E2574C;");
                statusLbl.setText("Bitte Kriterien und Punktzahl eingeben.");
                return;
            }
            int scoreVal;
            try { scoreVal = Integer.parseInt(scoreText); }
            catch (NumberFormatException ex) {
                statusLbl.setStyle("-fx-text-fill: #E2574C;");
                statusLbl.setText("Punktzahl muss eine Zahl zwischen 0 und 10 sein.");
                return;
            }
            if (scoreVal < 0 || scoreVal > 10) {
                statusLbl.setStyle("-fx-text-fill: #E2574C;");
                statusLbl.setText("Punktzahl muss zwischen 0 und 10 liegen.");
                return;
            }
            try {
                if (scoreRef[0] != null) {
                    scoreRef[0].setCriteria(criteria);
                    scoreRef[0].setScoreValue(scoreVal);
                    scoreRef[0].setComment(commentArea.getText());
                    scoreRepo.save(scoreRef[0]);
                } else if (!subs.isEmpty()) {
                    Score newScore = new Score(criteria, scoreVal, commentArea.getText(),
                            java.time.LocalDateTime.now(), subs.get(0), juryObj);
                    scoreRepo.save(newScore);
                    scoreRef[0] = newScore;
                } else {
                    statusLbl.setStyle("-fx-text-fill: #E2574C;");
                    statusLbl.setText("Kein Projekt eingereicht — Bewertung nicht möglich.");
                    return;
                }
                statusLbl.setStyle("-fx-text-fill: #10B981;");
                statusLbl.setText("✓ Bewertung gespeichert!");
                scoreBtn.setText("Bewertung aktualisieren");
            } catch (Exception ex) {
                statusLbl.setStyle("-fx-text-fill: #E2574C;");
                statusLbl.setText("Fehler: " + ex.getMessage());
            }
        });

        detailContent.getChildren().addAll(
                criteriaLbl, criteriaField,
                scoreLbl, scoreField,
                commentLbl, commentArea,
                scoreBtn, statusLbl
        );
    }

    private void hideDetail() {
        detailPanel.setVisible(false);
        detailPanel.setManaged(false);
        detailContent.getChildren().clear();
    }

    private void setActiveButton(Button active) {
        for (Button btn : new Button[]{btnEvents, btnSessions, btnTeams, btnScores}) {
            if (btn != null) btn.setStyle(INACTIVE_STYLE);
        }
        active.setStyle(ACTIVE_STYLE);
    }

    @FXML
    private void onLogout() {
        try {
            SessionContext.clear();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/whz/it_events/it_eventsdbapp/login-view.fxml")
            );
            Parent root = loader.load();
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/whz/it_events/it_eventsdbapp/styles.css").toExternalForm()
            );
            stage.setTitle("IT Events - Anmeldung");
            stage.setMaximized(false);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}

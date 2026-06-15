package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import whz.it_events.it_eventsdbapp.dao.VisitorRepository;
import whz.it_events.it_eventsdbapp.model.Event;
import whz.it_events.it_eventsdbapp.model.Jury;
import whz.it_events.it_eventsdbapp.model.Participant;
import whz.it_events.it_eventsdbapp.model.Session;
import whz.it_events.it_eventsdbapp.model.Team;
import whz.it_events.it_eventsdbapp.model.Track;
import whz.it_events.it_eventsdbapp.model.User;
import whz.it_events.it_eventsdbapp.model.Visitor;

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
        boolean registered = isRegisteredForEvent(currentUser, event);
        Button regBtn = makeRegBtn(registered ? "✓ Angemeldet" : "＋ Registrieren", registered);
        if (!registered) regBtn.setOnAction(e -> { registerForEvent(event, regBtn); e.consume(); });

        card.getChildren().addAll(name, location, dates, status, regBtn);
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
        contentTitle.setText("Meine Sessions");
        listContainer.getChildren().clear();
        hideDetail();

        User currentUser = SessionContext.getCurrentUser();
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

    // ─── MEINE TEAMS ─────────────────────────────────────────────────────────

    @FXML public void showTeams() {
        setActiveButton(btnTeams);
        contentTitle.setText("Meine Teams");
        listContainer.getChildren().clear();
        hideDetail();

        User currentUser = SessionContext.getCurrentUser();
        List<Long> myEventIds = participantRepo.findByUserId(currentUser.getId()).stream()
                .filter(p -> p.getTrack() != null && p.getTrack().getEvent() != null)
                .map(p -> p.getTrack().getEvent().getId())
                .distinct().collect(Collectors.toList());

        if (myEventIds.isEmpty()) {
            Label empty = new Label("Du bist noch bei keinem Event angemeldet.\nMelde dich zuerst bei einem Event an.");
            empty.setStyle("-fx-text-fill: #8A8FA3; -fx-font-size: 13px;");
            empty.setWrapText(true);
            listContainer.getChildren().add(empty);
            return;
        }

        List<Team> myTeams = teamRepo.findAll().stream()
                .filter(t -> t.getTrack() != null && t.getTrack().getEvent() != null &&
                        myEventIds.contains(t.getTrack().getEvent().getId()))
                .collect(Collectors.toList());

        if (myTeams.isEmpty()) {
            Label empty = new Label("Keine Teams in deinen Events gefunden.");
            empty.setStyle("-fx-text-fill: #8A8FA3; -fx-font-size: 13px;");
            listContainer.getChildren().add(empty);
            return;
        }

        for (Team team : myTeams) listContainer.getChildren().add(createTeamCard(team));
    }

    // ─── SCORES ──────────────────────────────────────────────────────────────

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

        // Tracks
        addSectionTitle(detailContent, "Tracks");
        VBox tracksBox = new VBox(8);
        for (Track track : trackRepo.findAll()) {
            if (track.getEvent() != null && track.getEvent().getId().equals(event.getId())) {
                VBox tCard = new VBox(4);
                tCard.setStyle("-fx-background-color: #F4F6FB; -fx-background-radius: 8; -fx-padding: 10 14;");
                Label tName = new Label("🎯 " + track.getName());
                tName.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");
                tCard.getChildren().add(tName);
                if (track.getDescription() != null && !track.getDescription().isBlank()) {
                    Label tDesc = new Label(track.getDescription());
                    tDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3;");
                    tDesc.setWrapText(true);
                    tCard.getChildren().add(tDesc);
                }
                if (track.getDeadlineDate() != null) {
                    Label dl = new Label("⏰ Deadline: " + track.getDeadlineDate().format(DATE_FMT));
                    dl.setStyle("-fx-font-size: 11px; -fx-text-fill: #E2574C;");
                    tCard.getChildren().add(dl);
                }
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
                if (!alreadyVisitor) {
                    // find the register button and wire it
                    sCard.getChildren().stream()
                            .filter(n -> n instanceof Button)
                            .map(n -> (Button) n)
                            .findFirst()
                            .ifPresent(btn -> btn.setOnAction(e -> {
                                try {
                                    visitorRepo.save(new Visitor(currentUser, session));
                                    btn.setText("✓ Angemeldet");
                                    btn.setStyle("-fx-background-color: #ECFDF5; -fx-text-fill: #10B981; " +
                                            "-fx-background-radius: 8; -fx-padding: 5 14; -fx-font-size: 12px; -fx-cursor: default;");
                                    btn.setDisable(true);
                                } catch (Exception ex) { btn.setText("⚠️ Fehler"); }
                            }));
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

        // Register button
        detailContent.getChildren().add(new Separator());
        User currentUser = SessionContext.getCurrentUser();
        boolean registered = isRegisteredForEvent(currentUser, event);
        Button regBtn = makeRegBtn(registered ? "✓ Bereits angemeldet" : "✅  An diesem Event teilnehmen", registered);
        regBtn.setMaxWidth(Double.MAX_VALUE);
        if (!registered) regBtn.setOnAction(e -> registerForEvent(event, regBtn));
        detailContent.getChildren().add(regBtn);
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
        Button sBtn = makeRegBtn(alreadyVisitor ? "✓ Angemeldet" : "＋ Für diese Session anmelden", alreadyVisitor);
        sBtn.setMaxWidth(Double.MAX_VALUE);
        if (!alreadyVisitor) {
            sBtn.setOnAction(e -> {
                try {
                    visitorRepo.save(new Visitor(currentUser, session));
                    sBtn.setText("✓ Angemeldet");
                    sBtn.setStyle("-fx-background-color: #ECFDF5; -fx-text-fill: #10B981; " +
                            "-fx-background-radius: 10; -fx-padding: 12 20; -fx-font-weight: bold; " +
                            "-fx-font-size: 13px; -fx-cursor: default;");
                    sBtn.setDisable(true);
                } catch (Exception ex) { sBtn.setText("⚠️ Fehler"); }
            });
        }
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
        VBox card = new VBox(6);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(46,49,66,0.06), 8, 0, 0, 2); -fx-padding: 16;");
        Label track = new Label("🎯 " + (jury.getTrack() != null ? jury.getTrack().getName() : "–"));
        track.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2E3142;");
        Label area = new Label(jury.getProfArea() != null ? "💼 " + jury.getProfArea() : "");
        area.setStyle("-fx-font-size: 12px; -fx-text-fill: #8A8FA3;");
        card.getChildren().addAll(track, area);
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

    private Button makeRegBtn(String text, boolean registered) {
        Button btn = new Button(text);
        btn.setStyle(registered
                ? "-fx-background-color: #ECFDF5; -fx-text-fill: #10B981; -fx-background-radius: 8; -fx-padding: 5 14; -fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: default;"
                : "-fx-background-color: #5B6EF5; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 5 14; -fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand;");
        btn.setDisable(registered);
        return btn;
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
            btn.setStyle("-fx-background-color: #ECFDF5; -fx-text-fill: #10B981; -fx-background-radius: 8; -fx-padding: 5 14; -fx-font-size: 12px; -fx-cursor: default;");
            btn.setDisable(true);
        } catch (Exception e) { btn.setText("⚠️ Fehler"); }
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

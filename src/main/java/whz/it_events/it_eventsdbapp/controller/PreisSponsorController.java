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
import whz.it_events.it_eventsdbapp.dao.PreisRepository;
import whz.it_events.it_eventsdbapp.dao.PreisSponsorRepository;
import whz.it_events.it_eventsdbapp.dao.SponsorRepository;
import whz.it_events.it_eventsdbapp.dao.TrackRepository;
import whz.it_events.it_eventsdbapp.model.*;

public class PreisSponsorController {
    @FXML private TableView<PreisSponsor> preisSponsorTable;
    @FXML private TableColumn<PreisSponsor, Long> colId;
    @FXML private TableColumn<PreisSponsor, String> colTrack;
    @FXML private TableColumn<PreisSponsor, String> colPreis;
    @FXML private TableColumn<PreisSponsor, String> colSponsor;
    @FXML private ComboBox<Track> trackComboBox;
    @FXML private ComboBox<Preis> preisComboBox;
    @FXML private ComboBox<Sponsor> sponsorComboBox;
    @FXML private VBox rightPanel;
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private EntityManager em;
    private PreisSponsorRepository preisSponsorRepo;
    private TrackRepository trackRepo;
    private PreisRepository preisRepo;
    private SponsorRepository sponsorRepo;
    private final ObservableList<PreisSponsor> data = FXCollections.observableArrayList();
    private PreisSponsor current;

    @FXML public void initialize() {
        em = JpaUtil.getEntityManager();
        preisSponsorRepo = new PreisSponsorRepository(em, PreisSponsor.class);
        trackRepo = new TrackRepository(em, Track.class);
        preisRepo = new PreisRepository(em, Preis.class);
        sponsorRepo = new SponsorRepository(em, Sponsor.class);

        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colTrack.setCellValueFactory(c -> { Track t = c.getValue().getTrack(); return new SimpleStringProperty(t != null ? t.getName() : ""); });
        colPreis.setCellValueFactory(c -> { Preis p = c.getValue().getPreis(); return new SimpleStringProperty(p != null ? p.getName() : ""); });
        colSponsor.setCellValueFactory(c -> { Sponsor s = c.getValue().getSponsor(); return new SimpleStringProperty(s != null ? s.getName() : ""); });
        preisSponsorTable.setItems(data);

        trackComboBox.setItems(FXCollections.observableArrayList(trackRepo.findAll()));
        trackComboBox.setConverter(new StringConverter<Track>() {
            @Override public String toString(Track t) { return t != null ? t.getName() : ""; }
            @Override public Track fromString(String s) { return null; }
        });
        preisComboBox.setItems(FXCollections.observableArrayList(preisRepo.findAll()));
        preisComboBox.setConverter(new StringConverter<Preis>() {
            @Override public String toString(Preis p) { return p != null ? p.getName() : ""; }
            @Override public Preis fromString(String s) { return null; }
        });
        sponsorComboBox.setItems(FXCollections.observableArrayList(sponsorRepo.findAll()));
        sponsorComboBox.setConverter(new StringConverter<Sponsor>() {
            @Override public String toString(Sponsor s) { return s != null ? s.getName() : ""; }
            @Override public Sponsor fromString(String s) { return null; }
        });

        preisSponsorTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> showInForm(n));
        onNew();
        applyRoleAccess(); load();
    }

    private void load() { data.setAll(preisSponsorRepo.findAll()); }
    private void showInForm(PreisSponsor ps) { current = ps; if (ps == null) { clearForm(); return; } trackComboBox.setValue(ps.getTrack()); preisComboBox.setValue(ps.getPreis()); sponsorComboBox.setValue(ps.getSponsor()); statusLabel.setText(""); }
    private void clearForm() { trackComboBox.setValue(null); preisComboBox.setValue(null); sponsorComboBox.setValue(null); statusLabel.setText(""); }

    @FXML private void onNew() { current = null; preisSponsorTable.getSelectionModel().clearSelection(); clearForm(); }
    @FXML private void onSave() {
        Track track = trackComboBox.getValue();
        Preis preis = preisComboBox.getValue();
        Sponsor sponsor = sponsorComboBox.getValue();
        if (track == null || preis == null || sponsor == null) { statusLabel.setText("Alle Felder sind Pflichtfelder."); return; }
        PreisSponsor ps = (current != null) ? current : new PreisSponsor(track, preis, sponsor);
        if (current != null) { ps.setTrack(track); ps.setPreis(preis); ps.setSponsor(sponsor); }
        try { preisSponsorRepo.save(ps); statusLabel.setText("Gespeichert."); load(); onNew();
        applyRoleAccess(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }
    @FXML private void onDelete() {
        if (current == null) { statusLabel.setText("Bitte zuerst auswählen."); return; }
        try { preisSponsorRepo.delete(current); statusLabel.setText("Gelöscht."); load(); onNew();
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

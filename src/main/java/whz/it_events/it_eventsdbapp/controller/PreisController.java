package whz.it_events.it_eventsdbapp.controller;

import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import whz.it_events.it_eventsdbapp.config.JpaUtil;
import whz.it_events.it_eventsdbapp.dao.PreisRepository;
import whz.it_events.it_eventsdbapp.model.Preis;

public class PreisController {

    @FXML private TableView<Preis> preisTable;
    @FXML private TableColumn<Preis, Long> colId;
    @FXML private TableColumn<Preis, String> colName;
    @FXML private TableColumn<Preis, String> colCategory;
    @FXML private TableColumn<Preis, String> colDescription;

    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField descriptionField;
    @FXML private Label statusLabel;

    private EntityManager entityManager;
    private PreisRepository preisRepository;
    private final ObservableList<Preis> data = FXCollections.observableArrayList();
    private Preis current;

    @FXML
    public void initialize() {
        entityManager = JpaUtil.getEntityManager();
        preisRepository = new PreisRepository(entityManager, Preis.class);

        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPreisCategory()));
        colDescription.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        preisTable.setItems(data);
        preisTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> showInForm(n));
        onNew();
        load();
    }

    private void load() { data.setAll(preisRepository.findAll()); }

    private void showInForm(Preis p) {
        current = p;
        if (p == null) { clearForm(); return; }
        nameField.setText(p.getName());
        categoryField.setText(p.getPreisCategory());
        descriptionField.setText(p.getDescription());
        statusLabel.setText("");
    }

    private void clearForm() {
        nameField.clear(); categoryField.clear(); descriptionField.clear();
        statusLabel.setText("");
    }

    @FXML private void onNew() {
        current = null;
        preisTable.getSelectionModel().clearSelection();
        clearForm();
    }

    @FXML private void onSave() {
        String name = nameField.getText();
        if (name == null || name.isBlank()) { statusLabel.setText("Name darf nicht leer sein."); return; }

        Preis p = (current != null) ? current : new Preis();
        p.setName(name);
        p.setPreisCategory(categoryField.getText());
        p.setDescription(descriptionField.getText());

        try { preisRepository.save(p); statusLabel.setText("Gespeichert."); load(); onNew(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }

    @FXML private void onDelete() {
        if (current == null) { statusLabel.setText("Bitte zuerst auswählen."); return; }
        try { preisRepository.delete(current); statusLabel.setText("Gelöscht."); load(); onNew(); }
        catch (Exception e) { statusLabel.setText("Fehler: " + e.getMessage()); }
    }
}

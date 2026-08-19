package com.example.pension.controller;

import com.example.pension.controller.Popup.Popup;
import com.example.pension.dao.PersonneDAO;
import com.example.pension.model.Personne;
import com.example.pension.util.WinPopup;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public class listPers {

    @FXML private TableView<Personne> personneTable;
    @FXML private TableColumn<Personne, String> colStatut;
    @FXML private TableColumn<Personne, String> colNom;
    @FXML private TableColumn<Personne, String> colPrenom;
    @FXML private TableColumn<Personne, String> colDiplome;
    @FXML private TableColumn<Personne, String> colIM;

    @FXML private Button infoBtn;
    @FXML private Button modifBtn;
    @FXML private Button delBtn;
    @FXML private Button addBtn;
    @FXML private ComboBox<String> statutCombo;
    @FXML private TextField searchField;
    @FXML private Text effectif;

    private final PersonneDAO persDao = new PersonneDAO();
    private final ObservableList<Personne> masterList = FXCollections.observableArrayList();
    private FilteredList<Personne> filteredList;

    @FXML
    public void initialize() {
        colIM.setCellValueFactory(new PropertyValueFactory<>("im"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenoms"));
        colDiplome.setCellValueFactory(new PropertyValueFactory<>("diplome"));
        colStatut.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isStatut() ? "Vivant" : "Décédé")
        );

        initComboBox();
        setupFiltering();
        loadData();
    }

    private void initComboBox() {
        statutCombo.getItems().clear();
        statutCombo.getItems().addAll("Tous", "Vivant", "Décédé");
        statutCombo.setValue("Tous");
    }

    private void setupFiltering() {
        filteredList = new FilteredList<>(masterList, p -> true);
        personneTable.setItems(filteredList);

        // Écoute la recherche textuelle en temps réel
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filtrerDonnees());
    }

    private void loadData() {
        List<Personne> liste = persDao.findAll();
        masterList.clear();
        if (liste != null) {
            masterList.addAll(liste);
        }
        filtrerDonnees();
    }

    @FXML
    public void filtrerDonnees() {
        String selection = statutCombo.getValue();
        String motCle = searchField.getText() == null ? "" : searchField.getText().toLowerCase().trim();

        filteredList.setPredicate(p -> {
            // 1. Filtre par statut
            if (selection != null) {
                if (selection.equals("Vivant") && !p.isStatut()) return false;
                if (selection.equals("Décédé") && p.isStatut()) return false;
            }

            // 2. Filtre par recherche texte (Nom, Prénom ou IM)
            if (!motCle.isEmpty()) {
                String nom = p.getNom() != null ? p.getNom().toLowerCase() : "";
                String prenom = p.getPrenoms() != null ? p.getPrenoms().toLowerCase() : "";
                String im = p.getIm() != null ? p.getIm().toLowerCase() : "";

                return nom.contains(motCle) || prenom.contains(motCle) || im.contains(motCle);
            }

            return true;
        });

        updateEffectif();
    }

    private void updateEffectif() {
        effectif.setText("Effectifs : " + filteredList.size());
    }

    private void alertSel() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aucune sélection");
        alert.setHeaderText(null);
        alert.setContentText("Veuillez sélectionner une personne dans le tableau !");
        alert.showAndWait();
    }

    private Personne getSelected() {
        return personneTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void openInfo() {
        Personne selected = getSelected();
        if (selected == null) {
            alertSel();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pension/Personnes/infoPers.fxml"));
            Parent root = loader.load();

            infoPers controller = loader.getController();
            if (controller != null) {
                controller.setPersonne(selected);
            }

            personneTable.getScene().setRoot(root);

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de infoPers.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void modifPop() {
        Personne selected = getSelected();
        if (selected == null) {
            alertSel();
            return;
        }

        modifPers ctrl = WinPopup.openPopup("Personnes/modifPers.fxml", "Modifier Personne", (modifPers controller) -> {
            controller.setPersonne(selected);
        });

        if (ctrl != null && ctrl.isSaved()) {
            persDao.update(selected);
            loadData();
        }
    }

    @FXML
    public void delPop() {
        Personne selected = getSelected();
        if (selected == null) {
            alertSel();
        } else {
            Popup confirmCtrl = WinPopup.openPopup("confirmSupp.fxml", "Confirmation");

            if (confirmCtrl != null && confirmCtrl.isConfirmed()) {
                persDao.delete(selected.getIm());
                loadData();
            }
        }
    }

    @FXML
    public void addPop() {
        WinPopup.openPopup("Personnes/newPers.fxml", "Ajouter Personne", (addPers controller) -> {
            controller.setOnSuccess(this::loadData);
        });
    }
}
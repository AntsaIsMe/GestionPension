package com.example.pension.controller.Pension;

import com.example.pension.dao.PaiementDAO;
import com.example.pension.model.Paiement;
import com.example.pension.model.Personne;
import com.example.pension.model.Tarif;
import com.example.pension.util.PdfReceiptGenerator;
import com.example.pension.util.WinPopup;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class listPension {

    private final PaiementDAO payDao = new PaiementDAO();

    @FXML public Button modifier;
    @FXML public Button supprimer;
    @FXML public DatePicker dateDebut;
    @FXML public DatePicker dateFin;

    @FXML public TableView<Paiement> tablePension;
    @FXML public TableColumn<Paiement, LocalDate> colDate;
    @FXML public TableColumn<Paiement, String> colNom;
    @FXML public TableColumn<Paiement, String> colPrenom;
    @FXML public TableColumn<Paiement, String> colDiplome;
    @FXML public TableColumn<Paiement, Integer> colMontant;

    private final ObservableList<Paiement> paiementList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initColumns();
        loadD();
    }

    private void initColumns() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colDiplome.setCellValueFactory(new PropertyValueFactory<>("diplome"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));

        tablePension.setItems(paiementList);
    }

    private void loadD() {
        List<Paiement> pensions = payDao.findAll();
        paiementList.clear();
        if (pensions != null) {
            paiementList.addAll(pensions);
        }
    }

    @FXML
    public void filtrerParDate() {
        LocalDate debut = dateDebut.getValue();
        LocalDate fin = dateFin.getValue();

        if (debut != null && fin != null) {
            List<Paiement> pensions = payDao.findBetweenDates(debut, fin);
            paiementList.clear();
            if (pensions != null) {
                paiementList.addAll(pensions);
            }
        }
    }

    @FXML
    public void resetFiltre() {
        dateDebut.setValue(null);
        dateFin.setValue(null);
        loadD();
    }

    @FXML
    public void del() {
        Paiement selected = tablePension.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un paiement à supprimer.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer ce paiement ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean success = payDao.delete(selected.getIm(), selected.getNumTarif(), selected.getDate());
                if (success) {
                    loadD();
                }
            }
        });
    }

    @FXML
    public void modif() {
        Paiement selected = tablePension.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un paiement à modifier.");
            alert.showAndWait();
            return;
        }
        WinPopup.openPopup("Pension/modifPension.fxml", "Modifier", (modifPension controller) -> {
            controller.setInitData(selected);
            controller.setOnSuccess(this::loadD);
        });
    }

    @FXML
    public void addPension() {
        WinPopup.openPopup("Pension/addPension.fxml", "Payer une pension", (addPension controller) -> {
            controller.setOnSuccessCallback(this::loadD);
        });
    }

    @FXML
    public void genPdf() {
        Paiement selected = tablePension.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un paiement dans la liste.");
            alert.showAndWait();
            return;
        }

        // 1. Récupération des objets liés (Personne et Tarif)
        Personne personne = selected.getPersonne();
        Tarif tarif = selected.getTarif();

        if (personne == null || tarif == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible de générer le PDF : données de la personne ou du tarif introuvables.");
            alert.showAndWait();
            return;
        }

        // 2. Boîte de dialogue pour choisir l'emplacement du fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le reçu PDF");
        fileChooser.setInitialFileName("Recu_Pension_" + personne.getIm() + "_" + selected.getDate() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf"));

        // Utilisation de la scène actuelle pour afficher la boîte de dialogue
        File destFile = fileChooser.showSaveDialog(tablePension.getScene().getWindow());

        if (destFile != null) {
            try {
                // 3. Génération du PDF
                PdfReceiptGenerator generator = new PdfReceiptGenerator();
                generator.genererRecu(personne, tarif, selected, destFile.getAbsolutePath());

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Le reçu PDF a été généré avec succès !");
                alert.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la génération du PDF : " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}


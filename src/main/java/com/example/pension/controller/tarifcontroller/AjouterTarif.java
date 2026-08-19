package com.example.pension.controller.tarifcontroller;

import com.example.pension.dao.TarifDAO;
import com.example.pension.model.Tarif;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class AjouterTarif {

    @FXML private TextField champCategorie;
    @FXML private TextField champDiplome;
    @FXML private TextField champMontant;
    @FXML private Button btnAjouter;
    @FXML private Button btnAnnuler;

    private final TarifDAO tarifDAO = new TarifDAO();

    // Callback vers ListTarif, pour lui dire de rafraîchir son tableau une fois l'ajout terminé
    private Runnable actionApresAjout;

    public void setActionApresAjout(Runnable actionApresAjout) {
        this.actionApresAjout = actionApresAjout;
    }

    @FXML
    private void handleAjouter() {
        String categorie = champCategorie.getText().trim();
        String diplome = champDiplome.getText().trim();
        String texteMontant = champMontant.getText().trim();

        if (diplome.isEmpty() || texteMontant.isEmpty()) {
            afficherAlerte("Champs manquants", "Diplôme et montant sont obligatoires.", Alert.AlertType.WARNING);
            return;
        }

        int montant;
        try {
            montant = Integer.parseInt(texteMontant);
        } catch (NumberFormatException e) {
            afficherAlerte("Montant invalide", "Le montant doit être un nombre entier.", Alert.AlertType.WARNING);
            return;
        }

        String numTarif = genererNumTarif();
        Tarif nouveauTarif = new Tarif(numTarif, diplome, categorie, montant);

        boolean succes = tarifDAO.create(nouveauTarif);
        if (succes) {
            if (actionApresAjout != null) {
                actionApresAjout.run(); // prévient ListTarif de rafraîchir son tableau
            }
            fermerFenetre();
        } else {
            afficherAlerte("Erreur", "L'ajout du tarif a échoué.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAnnuler() {
        fermerFenetre();
    }

    /**
     * Génère un numéro de tarif du type T004, T005... en se basant
     * sur le nombre de tarifs déjà existants.
     */
    private String genererNumTarif() {
        List<Tarif> tousLesTarifs = tarifDAO.findAll();
        int prochainNumero = tousLesTarifs.size() + 1;
        return String.format("T%03d", prochainNumero);
    }

    private void fermerFenetre() {
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        stage.close();
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}
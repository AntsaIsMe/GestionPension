package com.example.pension.controller.tarifcontroller;

import com.example.pension.dao.TarifDAO;
import com.example.pension.model.Tarif;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModifTarif {

    @FXML private TextField champCategorie;
    @FXML private TextField champDiplome;
    @FXML private TextField champMontant;
    @FXML private Button btnModifier;
    @FXML private Button btnAnnuler;

    private final TarifDAO tarifDAO = new TarifDAO();

    // Le tarif qu'on est en train de modifier (transmis par ListTarif juste après l'ouverture de la fenêtre)
    private Tarif tarifAModifier;

    // Callback vers ListTarif, pour lui dire de rafraîchir son tableau une fois la modification terminée
    private Runnable actionApresModification;

    public void setActionApresModification(Runnable actionApresModification) {
        this.actionApresModification = actionApresModification;
    }

    /**
     * Reçoit le tarif sélectionné dans le tableau et pré-remplit les champs avec ses valeurs actuelles.
     * Doit être appelée par ListTarif juste après avoir chargé ce FXML, avant d'afficher la fenêtre.
     */
    public void setTarifAModifier(Tarif tarif) {
        this.tarifAModifier = tarif;
        champCategorie.setText(tarif.getCategorie());
        champDiplome.setText(tarif.getDiplome());
        champMontant.setText(String.valueOf(tarif.getMontant()));
    }

    @FXML
    private void handleModifier() {
        System.out.println("eeee");
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

        // Le numTarif ne change jamais (c'est la clé primaire), seuls les autres champs sont mis à jour
        tarifAModifier.setCategorie(categorie);
        tarifAModifier.setDiplome(diplome);
        tarifAModifier.setMontant(montant);

        boolean succes = tarifDAO.update(tarifAModifier);
        if (succes) {
            if (actionApresModification != null) {
                actionApresModification.run();
            }
            fermerFenetre();
        } else {
            afficherAlerte("Erreur", "La modification a échoué.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAnnuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) btnModifier.getScene().getWindow();
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


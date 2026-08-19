package com.example.pension.controller.Pension;

import com.example.pension.dao.PaiementDAO;
import com.example.pension.model.Paiement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.time.LocalDate;

public class modifPension {

    private Paiement paiement;
    private final PaiementDAO paiementDAO = new PaiementDAO();
    private Runnable onSucces;

    @FXML private DatePicker datePicker;
    @FXML private Button modifBtn;
    @FXML private Button cancelBtn;

    public void setInitData(Paiement p) {
        this.paiement = p;
        if (this.paiement != null && this.paiement.getDate() != null) {
            datePicker.setValue(this.paiement.getDate());
        }
    }

    public void setOnSuccess(Runnable callback) {
        this.onSucces = callback;
    }

    @FXML
    public void modif(ActionEvent event) {
        if (paiement == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun paiement sélectionné.");
            return;
        }

        LocalDate nouvelleDate = datePicker.getValue();
        if (nouvelleDate == null) {
            showAlert(Alert.AlertType.WARNING, "Date manquante", "Veuillez choisir une date.");
            return;
        }

        // La date fait partie de la clé primaire (im, num_tarif, datepayer) : il faut
        // l'ancienne valeur pour identifier la ligne à modifier, avant de l'écraser.
        LocalDate ancienneDate = paiement.getDate();

        if (ancienneDate != null && ancienneDate.equals(nouvelleDate)) {
            // Rien n'a changé, pas besoin d'appeler la base
            fermerFenetre(event);
            return;
        }

        boolean success = paiementDAO.updateDate(
                paiement.getIm(),
                paiement.getNumTarif(),
                ancienneDate,
                nouvelleDate
        );

        if (success) {
            paiement.setDate(nouvelleDate);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "La date de paiement a été mise à jour.");
            if (onSucces != null) {
                onSucces.run();
            }
            fermerFenetre(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Échec", "La mise à jour a échoué en base de données. " +
                    "Un paiement existe peut-être déjà à cette date pour cette personne.");
        }
    }

    @FXML
    public void cancel(ActionEvent event) {
        fermerFenetre(event);
    }

    /**
     * Ferme la fenêtre modale courante.
     */
    private void fermerFenetre(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Utilitaire pour afficher une alerte pop-up.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
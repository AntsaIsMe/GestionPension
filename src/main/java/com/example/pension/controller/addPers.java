package com.example.pension.controller;

import com.example.pension.dao.PersonneDAO;
import com.example.pension.model.Personne;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.UUID;

public class addPers {

    @FXML private TextField nomInput;
    @FXML private TextField prenomInput;
    @FXML private DatePicker dateNaisPicker;
    @FXML private TextField diplomeInput;
    @FXML private TextField contactInput;
    @FXML private ComboBox<String> situationCombo;
    @FXML private TextField nomConjointInput;
    @FXML private TextField prenomConjointInput;
    @FXML private Button addBtn;
    @FXML private Button cancelBtn;

    private final PersonneDAO personneDAO = new PersonneDAO();
    private boolean added = false;

    private Runnable onSuccess;

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    @FXML
    public void initialize() {
        // Remplit combo situation
        situationCombo.getItems().addAll("Celibataire", "Marié(e)", "Veuf", "Divorcé(e)");
    }

    // Clic bouton ajouter
    @FXML
    public void onAdd() {
        String nom = nomInput.getText();
        String prenom = prenomInput.getText();
        LocalDate dateNais = dateNaisPicker.getValue();
        String diplome = diplomeInput.getText();
        String contact = contactInput.getText();
        String situation = situationCombo.getValue();
        String nomConjoint = nomConjointInput.getText();
        String prenomConjoint = prenomConjointInput.getText();

        // Verification champs vides simples
        if (nom == null || nom.isBlank()) {
            return;
        }

        // Genere IM unique automatiquement
        String imAuto = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Cree objet personne (statut true = vivant par defaut)
        Personne p = new Personne(
                imAuto,
                nom,
                prenom,
                dateNais,
                diplome,
                contact,
                true,
                situation,
                nomConjoint,
                prenomConjoint
        );

        // Enregistre BDD
        boolean success = personneDAO.create(p);
        if (success) {
            if (onSuccess != null) {
                onSuccess.run();
            }
            fermerFenetre();
        }
    }

    // Clic bouton annuler
    @FXML
    public void onCancel() {
        this.added = false;
        fermerFenetre();
    }

    public boolean isAdded() {
        return added;
    }

    private void fermerFenetre() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}
package com.example.pension.controller;

import com.example.pension.model.Personne;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class modifPers {

    @FXML private TextField nomInput;
    @FXML private TextField prenomInput;
    @FXML private TextField contactInput;
    @FXML private ComboBox<String> diplomeCombo;
    @FXML private ComboBox<String> situationCombo;
    @FXML private ComboBox<String> statutCombo;
    @FXML private CheckBox decedeCheck;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private Personne personne;
    private boolean saved = false;

    @FXML
    public void initialize() {
        // Remplir combo choix
        diplomeCombo.getItems().addAll("CEPE", "BEPC", "BACC", "LICENCE", "MASTER", "DOCTORAT");
        situationCombo.getItems().addAll("Celibataire", "Marie", "Veuf", "Divorce");
        statutCombo.getItems().addAll("Retraite", "Actif", "Pensionne");
    }

    // Charge donnees personne dans champ
    public void setPersonne(Personne p) {
        this.personne = p;
        if (p != null) {
            nomInput.setText(p.getNom());
            prenomInput.setText(p.getPrenoms());
            contactInput.setText(p.getContact());
            diplomeCombo.setValue(p.getDiplome());
            situationCombo.setValue(p.getSituation());
            decedeCheck.setSelected(!p.isStatut()); // false = decede
        }
    }

    // Clique bouton modifier
    @FXML
    public void onSave() {
        if (personne != null) {
            personne.setNom(nomInput.getText());
            personne.setPrenoms(prenomInput.getText());
            personne.setContact(contactInput.getText());
            personne.setDiplome(diplomeCombo.getValue());
            personne.setSituation(situationCombo.getValue());
            personne.setStatut(!decedeCheck.isSelected()); // true = vivant

            this.saved = true;
            fermerFenetre();
        }
    }

    // Clique bouton annuler
    @FXML
    public void onCancel() {
        this.saved = false;
        fermerFenetre();
    }

    public boolean isSaved() {
        return saved;
    }

    private void fermerFenetre() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}
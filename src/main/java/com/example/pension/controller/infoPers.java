package com.example.pension.controller;

import com.example.pension.model.Personne;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class infoPers {
    @FXML private Text fullName;
    @FXML private Button modifBtn;
    @FXML private Button delBtn;
    @FXML private Text naiss;
    @FXML private Text contact;
    @FXML private Text situation;
    @FXML private Text vivant;
    @FXML private Text conjointName;
    @FXML private Text conjointStatut;

    // Stocke personne
    private Personne personne;

    @FXML
    public void initialize() {
        
    }

    // Recoit personne et met donnees dans ecran
    public void setPersonne(Personne p) {
        this.personne = p;

        if (p != null) {
            // Affiche nom et prenom
            String nomComp = (p.getNom() != null ? p.getNom() : "") + " " + (p.getPrenoms() != null ? p.getPrenoms() : "");
            fullName.setText(nomComp.trim());

            // Date naissance
            naiss.setText(p.getDateNais() != null ? p.getDateNais().toString() : "-");

            // Contact
            contact.setText(p.getContact() != null ? p.getContact() : "-");

            // Situation
            situation.setText(p.getSituation() != null ? p.getSituation() : "-");

            // Statut vivant ou decede
            vivant.setText(p.isStatut() ? "Vivant" : "Decede");

            // Info conjoint
            if (p.getNomConjoint() != null && !p.getNomConjoint().isBlank()) {
                String nomC = p.getNomConjoint();
                String prenomC = p.getPrenomConjoint() != null ? p.getPrenomConjoint() : "";
                conjointName.setText(nomC + " " + prenomC);

                // Conjoint statut par defaut vivant
                if (conjointStatut != null) {
                    conjointStatut.setText("Vivant");
                }
            } else {
                conjointName.setText("Aucun");
                if (conjointStatut != null) {
                    conjointStatut.setText("-");
                }
            }
        }
    }
}
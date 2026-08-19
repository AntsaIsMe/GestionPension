package com.example.pension.controller.Pension;

import com.example.pension.dao.PaiementDAO;
import com.example.pension.model.Paiement;
import com.example.pension.model.Personne;
import com.example.pension.model.Tarif;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addPension {
    @FXML public Button addBtn;
    @FXML public ComboBox<String> listeP;
    @FXML Text fullName;
    @FXML Text valPension;

    private final PaiementDAO payDao = new PaiementDAO();
    private final ObservableList<String> toutesLesEntrees = FXCollections.observableArrayList();
    private final Map<String, Personne> personneParLibelle = new HashMap<>();
    private final Map<String, Tarif> tarifParLibelle = new HashMap<>();

    // 1. Déclaration du Callback pour rafraîchir le parent
    private Runnable onSuccessCallback;

    public void setOnSuccessCallback(Runnable callback) {
        this.onSuccessCallback = callback;
    }

    @FXML
    public void initialize() {
        setInit();
        setupFiltre();
        setupSelection();
    }

    private void setInit() {
        listeP.getItems().clear();
        toutesLesEntrees.clear();
        personneParLibelle.clear();
        tarifParLibelle.clear();

        List<Paiement> entrees = payDao.findPersonnesAPayer();
        if (entrees == null) return;

        for (Paiement entree : entrees) {
            Personne p = entree.getPersonne();
            if (p == null) continue;
            Tarif t = entree.getTarif();

            String prenom = p.getPrenoms() != null ? p.getPrenoms() : "";
            String libelle = p.getNom() + " " + prenom + " (" + p.getIm() + ")";

            toutesLesEntrees.add(libelle);
            personneParLibelle.put(libelle, p);
            if (t != null) {
                tarifParLibelle.put(libelle, t);
            }
        }

        listeP.setItems(FXCollections.observableArrayList(toutesLesEntrees));
    }

    private void setupFiltre() {
        listeP.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null) return;

            if (toutesLesEntrees.contains(newText)) {
                return;
            }

            String recherche = newText.toLowerCase();
            List<String> filtres = toutesLesEntrees.stream()
                    .filter(libelle -> libelle.toLowerCase().contains(recherche))
                    .toList();

            listeP.setItems(FXCollections.observableArrayList(filtres));

            listeP.getEditor().setText(newText);
            listeP.getEditor().positionCaret(newText.length());
            if (!filtres.isEmpty()) {
                listeP.show();
            } else {
                listeP.hide();
            }
        });
    }

    private void setupSelection() {
        listeP.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || !personneParLibelle.containsKey(newVal)) {
                fullName.setText("");
                valPension.setText("");
                addBtn.setDisable(false);
                return;
            }

            Personne p = personneParLibelle.get(newVal);
            Tarif t = tarifParLibelle.get(newVal);

            String prenom = p.getPrenoms() != null ? p.getPrenoms() : "";
            fullName.setText(p.getNom() + " " + prenom);

            if (t != null) {
                valPension.setText(t.getMontant() + " Ar");
                addBtn.setDisable(false);
            } else {
                valPension.setText("Aucun tarif pour le diplôme \"" + p.getDiplome() + "\"");
                addBtn.setDisable(true);
            }
        });
    }

    @FXML
    public void onPay(ActionEvent event) {
        String libelle = listeP.getValue();

        if (libelle == null || !personneParLibelle.containsKey(libelle)) {
            afficherAlerte(Alert.AlertType.WARNING, "Sélection requise",
                    "Veuillez choisir un retraité valide dans la liste.");
            return;
        }

        Personne p = personneParLibelle.get(libelle);
        Tarif t = tarifParLibelle.get(libelle);

        if (t == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Tarif manquant",
                    "Aucun tarif n'est défini pour le diplôme \"" + p.getDiplome() + "\". Impossible de payer.");
            return;
        }

        Paiement paiement = new Paiement(p.getIm(), t.getNumTarif(), LocalDate.now());
        boolean ok = payDao.create(paiement);

        if (ok) {
            afficherAlerte(Alert.AlertType.INFORMATION, "Paiement effectué",
                    "La pension de " + p.getNom() + " " + p.getPrenoms() + " a bien été payée.");

            // 2. Déclencher le rechargement du parent
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }

            // 3. Fermer la fenêtre
            fermerFenetre(event);
        } else {
            afficherAlerte(Alert.AlertType.ERROR, "Échec du paiement",
                    "Une erreur est survenue lors de l'enregistrement du paiement.");
        }
    }

    private void fermerFenetre(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
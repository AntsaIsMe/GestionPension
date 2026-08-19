package com.example.pension.controller.tarifcontroller;

import com.example.pension.dao.TarifDAO;
import com.example.pension.model.Tarif;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListTarif implements javafx.fxml.Initializable {

    // Ces fx:id doivent correspondre EXACTEMENT à ceux de ListTarif.fxml
    @FXML private TableView<Tarif> tableTarif;
    @FXML private TableColumn<Tarif, String> colNum;
    @FXML private TableColumn<Tarif, String> colCat;
    @FXML private TableColumn<Tarif, String> colDip;
    @FXML private TableColumn<Tarif, Integer> colMon;

    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;

    private final TarifDAO tarifDAO = new TarifDAO();
    private final ObservableList<Tarif> donnees = FXCollections.observableArrayList();

    private Tarif tarifSelectionne = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNum.setCellValueFactory(new PropertyValueFactory<>("numTarif"));
        colCat.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colDip.setCellValueFactory(new PropertyValueFactory<>("diplome"));
        colMon.setCellValueFactory(new PropertyValueFactory<>("montant"));

        tableTarif.setItems(donnees);

        tableTarif.getSelectionModel().selectedItemProperty().addListener((obs, ancienneValeur, nouvelleValeur) -> {
            tarifSelectionne = nouvelleValeur;
        });

        rafraichirTableau();
    }

    private void rafraichirTableau() {
        List<Tarif> liste = tarifDAO.findAll();
        donnees.setAll(liste);
    }

    @FXML
    private void handleOuvrirAjout() {
       System.out.println("button");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/pension/Tarif/AjouterTarif.fxml") // adapte le chemin exact
            );
            Parent racine = loader.load();

            AjouterTarif controleurAjout = loader.getController();
            controleurAjout.setActionApresAjout(this::rafraichirTableau);

            Stage fenetreAjout = new Stage();
            fenetreAjout.setTitle("Ajouter un tarif");
            fenetreAjout.setScene(new Scene(racine));
            fenetreAjout.initModality(Modality.APPLICATION_MODAL);
            fenetreAjout.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOuvrirModifier() {
        if (tarifSelectionne == null) {
            afficherAlerte("Aucune sélection", "Sélectionne d'abord un tarif dans le tableau.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/pension/Tarif/ModifTarif.fxml") // adapte le chemin exact
            );
            Parent racine = loader.load();

            ModifTarif controleurModifier = loader.getController();
            controleurModifier.setTarifAModifier(tarifSelectionne);
            controleurModifier.setActionApresModification(this::rafraichirTableau);

            Stage fenetreModifier = new Stage();
            fenetreModifier.setTitle("Modifier un tarif");
            fenetreModifier.setScene(new Scene(racine));
            fenetreModifier.initModality(Modality.APPLICATION_MODAL);
            fenetreModifier.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSupprimer() {
        if (tarifSelectionne == null) {
            afficherAlerte("Aucune sélection", "Sélectionne d'abord un tarif dans le tableau.", Alert.AlertType.WARNING);
            return;
        }


        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer le tarif " + tarifSelectionne.getNumTarif() + " ?");
        Optional<ButtonType> reponse = confirmation.showAndWait();

        if (reponse.isPresent() && reponse.get() == ButtonType.OK) {
            boolean succes = tarifDAO.delete(tarifSelectionne.getNumTarif());
            if (succes) {
                rafraichirTableau();
            } else {
                afficherAlerte("Erreur", "La suppression a échoué.", Alert.AlertType.ERROR);
            }
        }
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}

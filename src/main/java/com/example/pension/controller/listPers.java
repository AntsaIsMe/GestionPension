package com.example.pension.controller;

import com.example.pension.controller.Popup.Popup;
import com.example.pension.dao.PersonneDAO;
import com.example.pension.model.Personne;
import com.example.pension.util.WinPopup;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.List;

public class listPers {

    @FXML private TableView<Personne> personneTable;
    @FXML private TableColumn<Personne, String> colStatut;
    @FXML private TableColumn<Personne, String> colNom;
    @FXML private TableColumn<Personne, String> colPrenom;
    @FXML private TableColumn<Personne, String> colDiplome;

    @FXML private Button infoBtn;
    @FXML private Button modifBtn;
    @FXML private Button delBtn;
    @FXML private Button addBtn;

    private final PersonneDAO persDao = new PersonneDAO();

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenoms"));
        colDiplome.setCellValueFactory(new PropertyValueFactory<>("diplome"));
        colStatut.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isStatut() ? "Vivant" : "Décédé")
        );

        loadData();
    }

    //get data from Db
    private void loadData() {
        List<Personne> liste = persDao.findAll();
        ObservableList<Personne> observableList = FXCollections.observableArrayList(liste);
        personneTable.setItems(observableList);
    }

    //popup alert error
    private void alertSel() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aucune selection");
        alert.setHeaderText(null);
        alert.setContentText("Veuillez selectionner une personne dans le tableau !");
        alert.showAndWait();
    }

    //get the person selected
    private Personne getSelected() {
        return personneTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void openInfo() {
        //get selected elem
        Personne selected = getSelected();
        if(selected == null){
            alertSel();
            return;
        }
        try {
            //open new Info
            //  Charger la vue FXML de la page d'information
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pension/Personnes/infoPers.fxml"));
            Parent root = loader.load();

            //send Pers
            infoPers controller = loader.getController();
            if (controller != null) {
                controller.setPersonne(selected);
            }

            //  Remplacer la racine de toute la fenêtre actuelle
            personneTable.getScene().setRoot(root);

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de infoPers.fxml : " + e.getMessage());
            e.printStackTrace();
        }

    }

    //modif
    @FXML
    public void modifPop() {
        // get selected elem
        Personne selected = getSelected();
        if (selected == null) {
            alertSel();
            return;
        }

        // Ouvre popup modification avec objet charge
        modifPers ctrl = WinPopup.openPopup("Personnes/modifPers.fxml", "Modifier Personne", (modifPers controller) -> {
            controller.setPersonne(selected);
        });

        // Si clic enregistrer, sauve en BDD
        if (ctrl != null && ctrl.isSaved()) {
            persDao.update(selected);
            loadData();
        }
    }

    @FXML
    public void delPop() {
        Personne selected = getSelected();
        if(selected == null){
            alertSel();
        }
        else{
            Popup confirmCtrl = WinPopup.openPopup("confirmSupp.fxml", "Confirmation");

            if (confirmCtrl != null && confirmCtrl.isConfirmed()) {
                persDao.delete(selected.getIm());
                loadData();
            }
        }
    }

    @FXML
    public void addPop(){
        WinPopup.openPopup("Personnes/newPers.fxml", "Ajouter Personne", (addPers controller) -> {
            controller.setOnSuccess(this::loadData);
        });
    }
}
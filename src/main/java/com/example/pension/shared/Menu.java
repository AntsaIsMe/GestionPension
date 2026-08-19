package com.example.pension.shared;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class Menu {

    @FXML
    private void redirect(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String pageName = btn.getId();

        System.out.println("Redirect vers : " + pageName);

        String fxmlPath = "";

        // Determine the path
        switch (pageName) {
            case "Pension":
                // Adapte le chemin vers ton vrai fichier FXML
                fxmlPath = "/com/example/pension/Pension/listPension.fxml";
                break;

            case "Personne":
                fxmlPath = "/com/example/pension/Personnes/listPers.fxml";
                break;

            case "Tarif":
                fxmlPath = "/com/example/pension/Tarif/Tarif.fxml";
                break;

            case "Histogramme":
                fxmlPath = "/com/example/pension/Histogramme.fxml";
                //System.out.println("/com/example/pension/Histogramme.fxml");

                break;

            default:
                System.out.println("Aucune page associée au bouton : " + pageName);
                return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur de chargement du fichier : " + fxmlPath);
            e.printStackTrace();
        }
    }
}
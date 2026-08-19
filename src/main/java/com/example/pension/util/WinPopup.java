package com.example.pension.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class WinPopup {

    // 1. Appel simple par défaut (bloquant par défaut, retourne le contrôleur)
    public static <T> T openPopup(String path, String title) {
        return openPopup(path, title, false, null);
    }

    // 2. Appel avec option de modalité (retourne le contrôleur)
    public static <T> T openPopup(String path, String title, boolean optional) {
        return openPopup(path, title, optional, null);
    }

    // 3. Appel avec injection de données/paramètres (ex: passer une Personne, retourne le contrôleur)
    public static <T> T openPopup(String path, String title, Consumer<T> controllerInitializer) {
        return openPopup(path, title, false, controllerInitializer);
    }

    /**
     * Méthode principale qui charge la fenêtre et retourne son Contrôleur.
     *
     * @param path Chemin relatif depuis "/com/example/pension/"
     * @param title Titre de la fenêtre
     * @param optional false = bloquant (APPLICATION_MODAL), true = non-bloquant
     * @param controllerInitializer Lambda/Action pour passer des données au contrôleur (Optionnel)
     * @return Le contrôleur de la vue chargée, ou null en cas d'erreur.
     */
    public static <T> T openPopup(String path, String title, boolean optional, Consumer<T> controllerInitializer) {
        try {
            var resource = WinPopup.class.getResource("/com/example/pension/" + path);

            if (resource == null) {
                System.err.println("Fichier FXML introuvable ! Vérifiez le chemin indiqué : " + path);
                return null;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            // Récupération du contrôleur générique
            T controller = loader.getController();

            // Transmission des données/paramètres si une fonction d'initialisation est fournie
            if (controllerInitializer != null && controller != null) {
                controllerInitializer.accept(controller);
            }

            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.setScene(new Scene(root));

            if (!optional) {
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.showAndWait(); // Attend la fermeture si bloquant
            } else {
                popupStage.show();
            }


            return controller;

        } catch (IOException e) {
            System.err.println("Erreur de chargement du fichier FXML : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
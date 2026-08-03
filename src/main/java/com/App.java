package com.pension;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the main view layout from FXML
        Parent root = FXMLLoader.load(getClass().getResource("/com/pension/view/main-view.fxml"));
        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Gestion Pension");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
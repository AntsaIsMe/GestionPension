package com.example.pension.controller.Popup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Popup {
    @FXML private Button cancel;
    @FXML private Button confirm;

    private boolean confirmed = false;

    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    public void onConfirm() {
        this.confirmed = true;
        fermerFenetre();
    }

    @FXML
    public void onCancel() {
        this.confirmed = false;
        fermerFenetre();
    }

    //close
    private void fermerFenetre() {
        Stage stage = (Stage) confirm.getScene().getWindow();
        stage.close();
    }

}

package com.example.pension.controller;

import com.example.pension.dao.StatistiqueDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class Histogramme implements Initializable {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private final StatistiqueDAO statistiqueDAO = new StatistiqueDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chargerHistogramme();
    }

    private void chargerHistogramme() {

        // Récupération des données depuis la base de données
        Map<String, Long> donnees =
                statistiqueDAO.montantTotalParMois();
        System.out.println(donnees);

        // Nettoyage du graphique
        barChart.getData().clear();


        // Création de la série
        XYChart.Series<String, Number> serie =
                new XYChart.Series<>();

        serie.setName("Montant payé");

        // Ajout des données dans le graphique
        for (Map.Entry<String, Long> entree : donnees.entrySet()) {

            serie.getData().add(
                    new XYChart.Data<>(
                            entree.getKey(),
                            entree.getValue()
                    )
            );
        }

        // Ajout de la série au graphique
        barChart.getData().add(serie);

        for (XYChart.Data<String, Number> data : serie.getData()) {
            data.getNode().setStyle("-fx-bar-fill: #86584D;");
        }

        // Nom des axes
        xAxis.setLabel("Mois");
        yAxis.setLabel("Montant total payé");
    }
}

package com.example.pension.dao;

import com.example.pension.database.DatabaseConnection;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatistiqueDAO {

    /** Nombre de pensionnaires vivants / décédés (pour l'histogramme). */
    public Map<String, Integer> effectifParStatut() {
        Map<String, Integer> resultat = new LinkedHashMap<>();
        String sql = "SELECT statut, COUNT(*) AS effectif FROM personne GROUP BY statut";
        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                resultat.put(rs.getBoolean("statut") ? "Vivant" : "Décédé", rs.getInt("effectif"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultat;
    }

    /** Montant total payé par mois (format 'YYYY-MM'), utile pour un histogramme des paiements. */
    public Map<String, Long> montantTotalParMois() {
        Map<String, Long> resultat = new LinkedHashMap<>();

        String sql =
                "SELECT to_char(p.datepayer, 'YYYY-MM') AS mois, " +
                        "SUM(t.montant) AS total " +
                        "FROM payer p " +
                        "JOIN tarif t ON p.num_tarif = t.num_tarif " +
                        "GROUP BY mois " +
                        "ORDER BY mois";

        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                resultat.put(
                        rs.getString("mois"),
                        rs.getLong("total")
                );
                //System.out.println(resultat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultat;
    }

    /** Effectif de pensionnaires par catégorie de diplôme (autre choix possible d'histogramme). */
    public Map<String, Integer> effectifParDiplome() {
        Map<String, Integer> resultat = new LinkedHashMap<>();
        String sql = "SELECT diplome, COUNT(*) AS effectif FROM personne GROUP BY diplome ORDER BY diplome";
        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                resultat.put(rs.getString("diplome"), rs.getInt("effectif"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultat;
    }
}

package com.example.pension.dao;

import com.example.pension.database.DatabaseConnection;
import com.example.pension.model.Paiement;
import com.example.pension.model.Personne;
import com.example.pension.model.Tarif;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaiementDAO {

    /**
     * Insère un nouveau paiement en base de données.
     * La PK est désormais (im, num_tarif, datepayer) : plusieurs paiements dans le temps
     * sont possibles pour la même personne/tarif, mais pas deux le même jour.
     */
    public boolean create(Paiement p) {
        String sql = "INSERT INTO payer (im, num_tarif, datepayer) VALUES (?, ?, ?)";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            // datepayer est NOT NULL en base : si non fournie, on prend aujourd'hui
            LocalDate date = p.getDate() != null ? p.getDate() : LocalDate.now();

            ps.setString(1, p.getIm());
            ps.setString(2, p.getNumTarif());
            ps.setDate(3, Date.valueOf(date));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Récupère tous les paiements enregistrés avec les détails des tables Personne et Tarif.
     */
    public List<Paiement> findAll() {
        List<Paiement> liste = new ArrayList<>();
        String sql = "SELECT p.im, p.num_tarif, p.datepayer, " +
                "pe.nom, pe.prenom, pe.datenais, pe.diplome, pe.contact, pe.statut, pe.situation, pe.nomconjoint, pe.prenomconjoint, " +
                "t.categorie, t.montant " +
                "FROM payer p " +
                "INNER JOIN personne pe ON p.im = pe.im " +
                "INNER JOIN tarif t ON p.num_tarif = t.num_tarif " +
                "ORDER BY p.datepayer DESC NULLS LAST";

        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                liste.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Recherche un paiement précis via sa clé primaire complète (im + num_tarif + datepayer).
     * Remplace l'ancien findById(im, numTarif), devenu ambigu depuis que plusieurs paiements
     * peuvent exister pour le même (im, num_tarif) à des dates différentes.
     */
    public Paiement findByKey(String im, String numTarif, LocalDate datepayer) {
        String sql = "SELECT p.im, p.num_tarif, p.datepayer, " +
                "pe.nom, pe.prenom, pe.datenais, pe.diplome, pe.contact, pe.statut, pe.situation, pe.nomconjoint, pe.prenomconjoint, " +
                "t.categorie, t.montant " +
                "FROM payer p " +
                "INNER JOIN personne pe ON p.im = pe.im " +
                "INNER JOIN tarif t ON p.num_tarif = t.num_tarif " +
                "WHERE p.im = ? AND p.num_tarif = ? AND p.datepayer = ?";

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, im);
            ps.setString(2, numTarif);
            ps.setDate(3, Date.valueOf(datepayer));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Historique complet des paiements d'une personne donnée, du plus récent au plus ancien.
     */
    public List<Paiement> findHistoriqueByIm(String im) {
        List<Paiement> liste = new ArrayList<>();
        String sql = "SELECT p.im, p.num_tarif, p.datepayer, " +
                "pe.nom, pe.prenom, pe.datenais, pe.diplome, pe.contact, pe.statut, pe.situation, pe.nomconjoint, pe.prenomconjoint, " +
                "t.categorie, t.montant " +
                "FROM payer p " +
                "INNER JOIN personne pe ON p.im = pe.im " +
                "INNER JOIN tarif t ON p.num_tarif = t.num_tarif " +
                "WHERE p.im = ? " +
                "ORDER BY p.datepayer DESC";

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, im);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Récupère la liste des paiements dont la date est comprise entre deux bornes incluses.
     */
    public List<Paiement> findBetweenDates(LocalDate debut, LocalDate fin) {
        List<Paiement> liste = new ArrayList<>();
        String sql = "SELECT p.im, p.num_tarif, p.datepayer, " +
                "pe.nom, pe.prenom, pe.datenais, pe.diplome, pe.contact, pe.statut, pe.situation, pe.nomconjoint, pe.prenomconjoint, " +
                "t.categorie, t.montant " +
                "FROM payer p " +
                "INNER JOIN personne pe ON p.im = pe.im " +
                "INNER JOIN tarif t ON p.num_tarif = t.num_tarif " +
                "WHERE p.datepayer BETWEEN ? AND ? " +
                "ORDER BY p.datepayer DESC";

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(debut));
            ps.setDate(2, Date.valueOf(fin));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Liste TOUTES les personnes vivantes (statut = true), qu'elles aient ou non
     * un tarif correspondant à leur diplôme. Utilisé pour l'écran "Payer une pension".
     * Ne consulte PAS la table payer. Si aucun tarif ne correspond au diplôme de la
     * personne, tarif/num_tarif seront null dans le Paiement retourné (date = null aussi,
     * puisqu'aucun paiement réel n'est encore créé).
     */
    public List<Paiement> findPersonnesAPayer() {
        List<Paiement> liste = new ArrayList<>();
        String sql = "SELECT pe.im, t.num_tarif, " +
                "pe.nom, pe.prenom, pe.datenais, pe.diplome, pe.contact, pe.statut, pe.situation, pe.nomconjoint, pe.prenomconjoint, " +
                "t.categorie, t.montant " +
                "FROM personne pe " +
                "LEFT JOIN tarif t ON pe.diplome = t.diplome " +
                "WHERE pe.statut = true " +
                "ORDER BY pe.nom";

        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                liste.add(mapRowSansDate(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Corrige la date d'un paiement existant, identifié par sa clé complète.
     * NB : comme datepayer fait maintenant partie de la PK, "changer la date" revient
     * en réalité à déplacer la ligne vers une nouvelle clé primaire.
     */
    public boolean updateDate(String im, String numTarif, LocalDate ancienneDate, LocalDate nouvelleDate) {
        String sql = "UPDATE payer SET datepayer = ? WHERE im = ? AND num_tarif = ? AND datepayer = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(nouvelleDate));
            ps.setString(2, im);
            ps.setString(3, numTarif);
            ps.setDate(4, Date.valueOf(ancienneDate));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime une ligne précise de la table payer par clé primaire complète.
     */
    public boolean delete(String im, String numTarif, LocalDate datepayer) {
        String sql = "DELETE FROM payer WHERE im = ? AND num_tarif = ? AND datepayer = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, im);
            ps.setString(2, numTarif);
            ps.setDate(3, Date.valueOf(datepayer));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mappe un enregistrement de ResultSet vers un objet Paiement instanciant aussi Personne et Tarif.
     */
    private Paiement mapRow(ResultSet rs) throws SQLException {
        // 1. Instanciation du modèle Paiement
        String im = rs.getString("im");
        String numTarif = rs.getString("num_tarif");
        Date dateSql = rs.getDate("datepayer");
        LocalDate datePayer = (dateSql != null) ? dateSql.toLocalDate() : null;

        Paiement paiement = new Paiement(im, numTarif, datePayer);
        remplirPersonneEtTarif(rs, paiement, im, numTarif);
        return paiement;
    }

    /**
     * Comme mapRow, mais pour les requêtes qui ne sélectionnent pas la colonne "datepayer"
     * (ex: findPersonnesAPayer, qui ne lit pas la table payer). Le tarif peut être absent
     * (LEFT JOIN) : dans ce cas paiement.getTarif() renverra null.
     */
    private Paiement mapRowSansDate(ResultSet rs) throws SQLException {
        String im = rs.getString("im");
        String numTarif = rs.getString("num_tarif"); // peut être null si LEFT JOIN sans correspondance

        Paiement paiement = new Paiement(im, numTarif, null);
        remplirPersonneEtTarif(rs, paiement, im, numTarif);
        return paiement;
    }

    private void remplirPersonneEtTarif(ResultSet rs, Paiement paiement, String im, String numTarif) throws SQLException {
        // Instanciation du modèle Personne
        Personne personne = new Personne();
        personne.setIm(im);
        personne.setNom(rs.getString("nom"));
        personne.setPrenoms(rs.getString("prenom"));

        Date dateNaisSql = rs.getDate("datenais");
        if (dateNaisSql != null) {
            personne.setDateNais(dateNaisSql.toLocalDate());
        }
        personne.setDiplome(rs.getString("diplome"));
        personne.setContact(rs.getString("contact"));
        personne.setStatut(rs.getBoolean("statut"));
        personne.setSituation(rs.getString("situation"));
        personne.setNomConjoint(rs.getString("nomconjoint"));
        personne.setPrenomConjoint(rs.getString("prenomconjoint"));
        paiement.setPersonne(personne);

        // Instanciation du modèle Tarif — absent si LEFT JOIN sans correspondance
        if (numTarif == null) {
            paiement.setTarif(null);
            return;
        }

        Tarif tarif = new Tarif();
        tarif.setNumTarif(numTarif);
        tarif.setDiplome(rs.getString("diplome"));
        tarif.setCategorie(rs.getString("categorie"));
        tarif.setMontant(rs.getInt("montant"));
        paiement.setTarif(tarif);
    }
}
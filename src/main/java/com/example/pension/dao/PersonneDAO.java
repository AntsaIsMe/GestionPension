package com.example.pension.dao;

import com.example.pension.database.DatabaseConnection;
import com.example.pension.model.Conjoint;
import com.example.pension.model.Personne;
import com.example.pension.model.Tarif;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PersonneDAO {

    private final TarifDAO tarifDAO = new TarifDAO();
    private final ConjointDAO conjointDAO = new ConjointDAO();

    public boolean create(Personne p) {
        String sql = "INSERT INTO personne (im, nom, prenoms, datenais, diplome, contact, statut, situation, nom_conjoint, prenom_conjoint) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            fillPersonneParams(ps, p, false);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Personne> findAll() {
        List<Personne> liste = new ArrayList<>();
        String sql = "SELECT * FROM personne ORDER BY nom";
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

    public Optional<Personne> findByIm(String im) {
        String sql = "SELECT * FROM personne WHERE im = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, im);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /** Recherche par numéro (IM) ou par nom, avec LIKE (insensible à la casse). */
    public List<Personne> rechercher(String motCle) {
        List<Personne> liste = new ArrayList<>();
        String sql = "SELECT * FROM personne WHERE im ILIKE ? OR nom ILIKE ? ORDER BY nom";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            String pattern = "%" + motCle + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) liste.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /** Liste des personnes groupées par statut (vivant/décédé) avec l'effectif total de chaque groupe. */
    public Map<String, Integer> effectifParStatut() {
        Map<String, Integer> resultat = new LinkedHashMap<>();
        String sql = "SELECT statut, COUNT(*) AS effectif FROM personne GROUP BY statut";
        try (Connection cn = DatabaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String libelle = rs.getBoolean("statut") ? "Vivant" : "Décédé";
                resultat.put(libelle, rs.getInt("effectif"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultat;
    }

    public boolean update(Personne p) {
        String sql = "UPDATE personne SET nom = ?, prenoms = ?, datenais = ?, diplome = ?, contact = ?, " +
                "statut = ?, situation = ?, nom_conjoint = ?, prenom_conjoint = ? WHERE im = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            fillPersonneParams(ps, p, true);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String im) {
        String sql = "DELETE FROM personne WHERE im = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, im);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Déclare le décès d'une personne : passe son statut à "décédé"
     * et crée automatiquement une entrée dans CONJOINT avec 40% du montant
     * de la pension (calculé via le diplôme de la personne dans TARIF).
     */
    public boolean declarerDeces(String im) {
        Optional<Personne> optPersonne = findByIm(im);
        if (optPersonne.isEmpty()) return false;
        Personne personne = optPersonne.get();

        Optional<Tarif> optTarif = tarifDAO.findByDiplome(personne.getDiplome());
        if (optTarif.isEmpty()) return false;

        int montantConjoint = (int) Math.round(optTarif.get().getMontant() * 0.40);

        personne.setStatut(false);
        boolean maj = update(personne);
        if (!maj) return false;

        Conjoint conjoint = new Conjoint(
                personne.getIm(),
                personne.getNomConjoint(),
                personne.getPrenomConjoint(),
                montantConjoint
        );
        return conjointDAO.create(conjoint);
    }

    private void fillPersonneParams(PreparedStatement ps, Personne p, boolean isUpdate) throws SQLException {
        int i = 1;
        if (!isUpdate) ps.setString(i++, p.getIm());
        ps.setString(i++, p.getNom());
        ps.setString(i++, p.getPrenoms());
        ps.setDate(i++, p.getDateNais() != null ? Date.valueOf(p.getDateNais()) : null);
        ps.setString(i++, p.getDiplome());
        ps.setString(i++, p.getContact());
        ps.setBoolean(i++, p.isStatut());
        ps.setString(i++, p.getSituation());
        ps.setString(i++, p.getNomConjoint());
        ps.setString(i++, p.getPrenomConjoint());
        if (isUpdate) ps.setString(i, p.getIm());
    }

    private Personne mapRow(ResultSet rs) throws SQLException {
        Date dateNais = rs.getDate("datenais");
        return new Personne(
                rs.getString("im"),
                rs.getString("nom"),
                rs.getString("prenoms"),
                dateNais != null ? dateNais.toLocalDate() : null,
                rs.getString("diplome"),
                rs.getString("contact"),
                rs.getBoolean("statut"),
                rs.getString("situation"),
                rs.getString("nom_conjoint"),
                rs.getString("prenom_conjoint")
        );
    }
}

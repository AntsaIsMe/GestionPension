package com.example.pension.dao;

import com.example.pension.database.DatabaseConnection;
import com.example.pension.model.Tarif;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TarifDAO {

    public boolean create(Tarif tarif) {
        String sql = "INSERT INTO tarif (num_tarif, diplome, categorie, montant) VALUES (?, ?, ?, ?)";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tarif.getNumTarif());
            ps.setString(2, tarif.getDiplome());
            ps.setString(3, tarif.getCategorie());
            ps.setInt(4, tarif.getMontant());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Tarif> findAll() {
        List<Tarif> liste = new ArrayList<>();
        String sql = "SELECT * FROM tarif ORDER BY num_tarif";
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

    public Optional<Tarif> findByNumTarif(String numTarif) {
        String sql = "SELECT * FROM tarif WHERE num_tarif = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, numTarif);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /** Retourne le premier tarif correspondant à un diplôme donné (utilisé pour calculer un montant de pension). */
    public Optional<Tarif> findByDiplome(String diplome) {
        String sql = "SELECT * FROM tarif WHERE diplome = ? LIMIT 1";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, diplome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean update(Tarif tarif) {
        String sql = "UPDATE tarif SET diplome = ?, categorie = ?, montant = ? WHERE num_tarif = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tarif.getDiplome());
            ps.setString(2, tarif.getCategorie());
            ps.setInt(3, tarif.getMontant());
            ps.setString(4, tarif.getNumTarif());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String numTarif) {
        String sql = "DELETE FROM tarif WHERE num_tarif = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, numTarif);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Tarif mapRow(ResultSet rs) throws SQLException {
        return new Tarif(
                rs.getString("num_tarif"),
                rs.getString("diplome"),
                rs.getString("categorie"),
                rs.getInt("montant")
        );
    }
}

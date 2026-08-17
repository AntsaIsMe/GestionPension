package com.example.pension.dao;

import com.example.pension.database.DatabaseConnection;
import com.example.pension.model.Paiement;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaiementDAO {

    public boolean create(Paiement p) {
        String sql = "INSERT INTO payer (im, num_tarif, date_paiement) VALUES (?, ?, ?)";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getIm());
            ps.setString(2, p.getNumTarif());
            ps.setDate(3, Date.valueOf(p.getDate()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Paiement> findAll() {
        List<Paiement> liste = new ArrayList<>();
        String sql = "SELECT * FROM payer ORDER BY date_paiement DESC";
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

    /** Liste des pensions payées entre deux dates (bornes incluses). */
    public List<Paiement> findBetweenDates(LocalDate debut, LocalDate fin) {
        List<Paiement> liste = new ArrayList<>();
        String sql = "SELECT * FROM payer WHERE date_paiement BETWEEN ? AND ? ORDER BY date_paiement";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(debut));
            ps.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) liste.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public boolean update(Paiement p) {
        String sql = "UPDATE payer SET im = ?, num_tarif = ?, date_paiement = ? WHERE id = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getIm());
            ps.setString(2, p.getNumTarif());
            ps.setDate(3, Date.valueOf(p.getDate()));
            ps.setInt(4, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM payer WHERE id = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Paiement mapRow(ResultSet rs) throws SQLException {
        Paiement p = new Paiement(
                rs.getString("im"),
                rs.getString("num_tarif"),
                rs.getDate("date_paiement").toLocalDate()
        );
        p.setId(rs.getInt("id"));
        return p;
    }
}

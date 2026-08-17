package com.example.pension.dao;

import com.example.pension.database.DatabaseConnection;
import com.example.pension.model.Conjoint;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConjointDAO {

    public boolean create(Conjoint c) {
        String sql = "INSERT INTO conjoint (num_pension, nom_conjoint, prenom_conjoint, montant) VALUES (?, ?, ?, ?)";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getNumPension());
            ps.setString(2, c.getNomConjoint());
            ps.setString(3, c.getPrenomConjoint());
            ps.setInt(4, c.getMontant());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Conjoint> findAll() {
        List<Conjoint> liste = new ArrayList<>();
        String sql = "SELECT * FROM conjoint ORDER BY id";
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

    public List<Conjoint> findByNumPension(String numPension) {
        List<Conjoint> liste = new ArrayList<>();
        String sql = "SELECT * FROM conjoint WHERE num_pension = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, numPension);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) liste.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    private Conjoint mapRow(ResultSet rs) throws SQLException {
        Conjoint c = new Conjoint(
                rs.getString("num_pension"),
                rs.getString("nom_conjoint"),
                rs.getString("prenom_conjoint"),
                rs.getInt("montant")
        );
        c.setId(rs.getInt("id"));
        return c;
    }
}

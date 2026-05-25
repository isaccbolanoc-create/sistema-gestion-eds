package com.gasstation.web.dao;

import com.gasstation.web.config.DatabaseConfig;
import com.gasstation.web.model.Inventario;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InventarioDAO {

    public List<Inventario> listar() throws SQLException {
        List<Inventario> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM inventario ORDER BY combustible_id")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Inventario buscarPorCombustibleId(int combustibleId) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM inventario WHERE combustible_id = ?")) {
            stmt.setInt(1, combustibleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public void actualizarCantidad(int combustibleId, double nuevaCantidad) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE inventario SET cantidad = ?, fecha_actualizacion = NOW() WHERE combustible_id = ?")) {
            stmt.setDouble(1, nuevaCantidad);
            stmt.setInt(2, combustibleId);
            stmt.executeUpdate();
        }
    }

    private Inventario mapear(ResultSet rs) throws SQLException {
        Inventario inv = new Inventario();
        inv.setId(rs.getInt("id"));
        inv.setCombustibleId(rs.getInt("combustible_id"));
        inv.setCantidad(rs.getDouble("cantidad"));
        try { inv.setUbicacion(rs.getString("ubicacion")); } catch (SQLException ignored) {}
        return inv;
    }
}

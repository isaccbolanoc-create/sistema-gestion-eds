package com.gasstation.web.dao;

import com.gasstation.web.config.DatabaseConfig;
import com.gasstation.web.model.MovimientoInventario;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MovimientoInventarioDAO {

    public List<MovimientoInventario> listar() throws SQLException {
        List<MovimientoInventario> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM movimientos_inventario ORDER BY fecha DESC")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public void insertar(MovimientoInventario m) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO movimientos_inventario (combustible_id, tipo, cantidad, motivo, usuario) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setInt(1, m.getCombustibleId());
            stmt.setString(2, m.getTipo());
            stmt.setDouble(3, m.getCantidad());
            stmt.setString(4, m.getMotivo());
            stmt.setString(5, m.getUsuario());
            stmt.executeUpdate();
        }
    }

    private MovimientoInventario mapear(ResultSet rs) throws SQLException {
        MovimientoInventario m = new MovimientoInventario();
        m.setId(rs.getInt("id"));
        m.setCombustibleId(rs.getInt("combustible_id"));
        m.setTipo(rs.getString("tipo"));
        m.setCantidad(rs.getDouble("cantidad"));
        m.setMotivo(rs.getString("motivo"));
        try { m.setUsuario(rs.getString("usuario")); } catch (SQLException ignored) {}
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) m.setFecha(ts.toLocalDateTime());
        return m;
    }
}

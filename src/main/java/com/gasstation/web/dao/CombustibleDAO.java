package com.gasstation.web.dao;

import com.gasstation.web.config.DatabaseConfig;
import com.gasstation.web.model.Combustible;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CombustibleDAO {

    public List<Combustible> listar() throws SQLException {
        List<Combustible> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM combustibles ORDER BY id")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Combustible buscarPorId(int id) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM combustibles WHERE id = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public void insertar(Combustible c) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO combustibles (nombre, tipo, precio_venta, precio_compra, unidad) VALUES (?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, c.getNombre());
            stmt.setString(2, c.getTipo());
            stmt.setDouble(3, c.getPrecioVenta());
            stmt.setDouble(4, c.getPrecioCompra());
            stmt.setString(5, c.getUnidad());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getInt(1));
            }
        }
    }

    public void actualizar(Combustible c) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE combustibles SET nombre=?, tipo=?, precio_venta=?, precio_compra=?, unidad=? WHERE id=?")) {
            stmt.setString(1, c.getNombre());
            stmt.setString(2, c.getTipo());
            stmt.setDouble(3, c.getPrecioVenta());
            stmt.setDouble(4, c.getPrecioCompra());
            stmt.setString(5, c.getUnidad());
            stmt.setInt(6, c.getId());
            stmt.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM combustibles WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Combustible mapear(ResultSet rs) throws SQLException {
        Combustible c = new Combustible();
        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre"));
        c.setTipo(rs.getString("tipo"));
        c.setPrecioVenta(rs.getDouble("precio_venta"));
        c.setPrecioCompra(rs.getDouble("precio_compra"));
        c.setUnidad(rs.getString("unidad"));
        return c;
    }
}

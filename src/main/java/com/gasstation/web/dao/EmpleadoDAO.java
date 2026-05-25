package com.gasstation.web.dao;

import com.gasstation.web.config.DatabaseConfig;
import com.gasstation.web.model.Empleado;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmpleadoDAO {

    public List<Empleado> listar() throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM empleados ORDER BY id")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Empleado buscarPorId(int id) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM empleados WHERE id = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public void insertar(Empleado e) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO empleados (nombre, telefono, email, cargo, salario, horario) VALUES (?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, e.getNombre());
            stmt.setString(2, e.getTelefono());
            stmt.setString(3, e.getEmail());
            stmt.setString(4, e.getCargo());
            stmt.setDouble(5, e.getSalario());
            stmt.setString(6, e.getHorario());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) e.setId(keys.getInt(1));
            }
        }
    }

    public void actualizar(Empleado e) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE empleados SET nombre=?, telefono=?, email=?, cargo=?, salario=?, horario=? WHERE id=?")) {
            stmt.setString(1, e.getNombre());
            stmt.setString(2, e.getTelefono());
            stmt.setString(3, e.getEmail());
            stmt.setString(4, e.getCargo());
            stmt.setDouble(5, e.getSalario());
            stmt.setString(6, e.getHorario());
            stmt.setInt(7, e.getId());
            stmt.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM empleados WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Empleado mapear(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre"));
        e.setTelefono(rs.getString("telefono"));
        e.setEmail(rs.getString("email"));
        e.setCargo(rs.getString("cargo"));
        e.setSalario(rs.getDouble("salario"));
        e.setHorario(rs.getString("horario"));
        return e;
    }
}

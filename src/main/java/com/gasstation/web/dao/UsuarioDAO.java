package com.gasstation.web.dao;

import com.gasstation.web.config.DatabaseConfig;
import com.gasstation.web.model.Usuario;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Repository
public class UsuarioDAO {

    public Usuario buscarPorUsername(String username) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM usuarios WHERE username = ?")) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public boolean validar(String username, String password) throws SQLException {
        Usuario u = buscarPorUsername(username);
        if (u == null || !u.isActivo()) return false;
        return u.getPasswordHash().equals(hashPassword(password));
    }

    public void cambiarPassword(int id, String nuevaPassword) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE usuarios SET password_hash = ? WHERE id = ?")) {
            stmt.setString(1, hashPassword(nuevaPassword));
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        int empId = rs.getInt("empleado_id");
        if (!rs.wasNull()) u.setEmpleadoId(empId);
        u.setRole(rs.getString("role"));
        u.setActivo(rs.getBoolean("activo"));
        return u;
    }
}

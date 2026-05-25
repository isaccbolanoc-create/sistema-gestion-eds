package com.gasstation.web.dao;

import com.gasstation.web.config.DatabaseConfig;
import com.gasstation.web.model.Venta;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VentaDAO {

    public List<Venta> listar() throws SQLException {
        List<Venta> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ventas ORDER BY fecha DESC")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public void insertar(Venta v) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO ventas (combustible_id, cliente_id, empleado_id, cantidad, precio_unitario, total, tipo_pago) VALUES (?, ?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, v.getCombustibleId());
            if (v.getClienteId() != null) stmt.setInt(2, v.getClienteId());
            else stmt.setNull(2, Types.INTEGER);
            stmt.setInt(3, v.getEmpleadoId());
            stmt.setDouble(4, v.getCantidad());
            stmt.setDouble(5, v.getPrecioUnitario());
            stmt.setDouble(6, v.getTotal());
            stmt.setString(7, v.getTipoPago() != null ? v.getTipoPago() : "efectivo");
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) v.setId(keys.getInt(1));
            }
        }
    }

    public double totalVentasPeriodo(LocalDateTime inicio, LocalDateTime fin) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COALESCE(SUM(total), 0) FROM ventas WHERE fecha BETWEEN ? AND ?")) {
            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0;
    }

    private Venta mapear(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setId(rs.getInt("id"));
        v.setCombustibleId(rs.getInt("combustible_id"));
        int cid = rs.getInt("cliente_id");
        if (!rs.wasNull()) v.setClienteId(cid);
        v.setEmpleadoId(rs.getInt("empleado_id"));
        v.setCantidad(rs.getDouble("cantidad"));
        v.setPrecioUnitario(rs.getDouble("precio_unitario"));
        v.setTotal(rs.getDouble("total"));
        v.setTipoPago(rs.getString("tipo_pago"));
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) v.setFecha(ts.toLocalDateTime());
        return v;
    }
}

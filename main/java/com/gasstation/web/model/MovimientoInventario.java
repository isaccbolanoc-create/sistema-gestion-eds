package com.gasstation.web.model;

import java.time.LocalDateTime;

public class MovimientoInventario {
    private int id;
    private int combustibleId;
    private String tipo;
    private double cantidad;
    private String motivo;
    private String usuario;
    private LocalDateTime fecha;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCombustibleId() { return combustibleId; }
    public void setCombustibleId(int combustibleId) { this.combustibleId = combustibleId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}

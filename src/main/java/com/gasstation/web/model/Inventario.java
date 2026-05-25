package com.gasstation.web.model;

public class Inventario {
    private int id;
    private int combustibleId;
    private double cantidad;
    private String ubicacion;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCombustibleId() { return combustibleId; }
    public void setCombustibleId(int combustibleId) { this.combustibleId = combustibleId; }
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}

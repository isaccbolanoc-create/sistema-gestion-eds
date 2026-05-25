package com.gasstation.web.model;

import java.time.LocalDateTime;

public class Venta {
    private int id;
    private int combustibleId;
    private Integer clienteId;
    private int empleadoId;
    private double cantidad;
    private double precioUnitario;
    private double total;
    private String tipoPago;
    private LocalDateTime fecha;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCombustibleId() { return combustibleId; }
    public void setCombustibleId(int combustibleId) { this.combustibleId = combustibleId; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public int getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(int empleadoId) { this.empleadoId = empleadoId; }
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}

package com.gasstation.web.model;

public class Empleado {
    private int id;
    private String nombre;
    private String telefono;
    private String email;
    private String cargo;
    private double salario;
    private String horario;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
}

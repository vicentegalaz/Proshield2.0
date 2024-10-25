package com.example.proshield;

public class Emergencia {
    private String nombre;
    private String rutUsuario;

    public Emergencia() {
        // Constructor vacío requerido para Firebase
    }

    public Emergencia(String nombre, String rutUsuario) {
        this.nombre = nombre;
        this.rutUsuario = rutUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRutUsuario() {
        return rutUsuario;
    }
}

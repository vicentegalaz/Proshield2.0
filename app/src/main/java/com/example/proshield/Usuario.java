package com.example.proshield;

public class Usuario {
    private String nombre;
    private String contraseña;
    private String numeroContacto;
    private String numeroEmergencia;
    private String poseeEnfermedad;
    private String tipo; // "usuario" o "encargado"
    private String fotoPerfil; // URL de la foto de perfil
    private String trabajo; // Nuevo campo para el trabajo

    // Constructor vacío requerido para Firebase
    public Usuario() {}

    // Constructor actualizado
    public Usuario(String nombre, String contraseña, String numeroContacto,
                   String numeroEmergencia, String poseeEnfermedad, String tipo,
                   String fotoPerfil, String trabajo) {
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.numeroContacto = numeroContacto;
        this.numeroEmergencia = numeroEmergencia;
        this.poseeEnfermedad = poseeEnfermedad;
        this.tipo = tipo;
        this.fotoPerfil = fotoPerfil;
        this.trabajo = trabajo; // Inicializa el nuevo campo
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getNumeroContacto() {
        return numeroContacto;
    }

    public void setNumeroContacto(String numeroContacto) {
        this.numeroContacto = numeroContacto;
    }

    public String getNumeroEmergencia() {
        return numeroEmergencia;
    }

    public void setNumeroEmergencia(String numeroEmergencia) {
        this.numeroEmergencia = numeroEmergencia;
    }

    public String getPoseeEnfermedad() {
        return poseeEnfermedad;
    }

    public void setPoseeEnfermedad(String poseeEnfermedad) {
        this.poseeEnfermedad = poseeEnfermedad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getTrabajo() {
        return trabajo; // Getter para el nuevo campo
    }

    public void setTrabajo(String trabajo) {
        this.trabajo = trabajo; // Setter para el nuevo campo
    }
}



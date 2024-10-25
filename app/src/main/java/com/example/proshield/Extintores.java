package com.example.proshield;

public class Extintores {
    private String id; // ID único del extintor
    private boolean activado; // Estado del extintor

    // Constructor vacío requerido por Firebase
    public Extintores() {}

    // Constructor
    public Extintores(String id, boolean activado) {
        this.id = id;
        this.activado = activado;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActivado() {
        return activado;
    }

    public void setActivado(boolean activado) {
        this.activado = activado;
    }
}

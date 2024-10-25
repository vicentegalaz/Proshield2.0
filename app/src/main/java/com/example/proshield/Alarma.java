package com.example.proshield;

public class Alarma {
    private String id; // ID único de la alarma
    private boolean activada;

    // Constructor vacío requerido por Firebase
    public Alarma() {}

    // Constructor
    public Alarma(String id, boolean activada) {
        this.id = id;
        this.activada = activada;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActivada() {
        return activada;
    }

    public void setActivada(boolean activada) {
        this.activada = activada;
    }
}


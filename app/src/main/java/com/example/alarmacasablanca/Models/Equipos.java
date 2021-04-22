package com.example.alarmacasablanca.Models;

import com.google.firebase.firestore.GeoPoint;

public class Equipos {
    private String Nombre;
    private GeoPoint Location;
    private Integer Fallas;
    private String Numero;
    private String Bateria;
    private Float Color;

    public Equipos() {
    }

    public Equipos(String nombre, GeoPoint location, Integer fallas, String numero, String bateria, Float color) {
        Nombre = nombre;
        Location = location;
        Fallas = fallas;
        Numero = numero;
        Bateria = bateria;
        Color = color;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public GeoPoint getLocation() {
        return Location;
    }

    public void setLocation(GeoPoint location) {
        Location = location;
    }

    public Integer getFallas() {
        return Fallas;
    }

    public void setFallas(Integer fallas) {
        Fallas = fallas;
    }

    public String getNumero() {
        return Numero;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }

    public String getBateria() {
        return Bateria;
    }

    public void setBateria(String bateria) {
        Bateria = bateria;
    }

    public Float getColor() {
        return Color;
    }

    public void setColor(Float color) {
        Color = color;
    }
}

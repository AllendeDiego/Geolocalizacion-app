package com.example.alarmacasablanca.Models;

import java.util.Date;

public class Eventos {
    private String Tipo;
    private Date Fecha;
    private String Numero;

    public Eventos() {
    }

    public Eventos(String tipo, Date fecha, String numero) {
        Tipo = tipo;
        Fecha = fecha;
        Numero = numero;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public Date getFecha() {
        return Fecha;
    }

    public void setFecha(Date fecha) {
        Fecha = fecha;
    }

    public String getNumero() {
        return Numero;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }
}

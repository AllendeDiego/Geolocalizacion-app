package com.example.alarmacasablanca.Models;

public class Usuarios {
    private String Usuario;
    private String Password;

    public Usuarios() {
    }

    public Usuarios(String Usuario, String Password) {
        this.Usuario = Usuario;
        this.Password = Password;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}

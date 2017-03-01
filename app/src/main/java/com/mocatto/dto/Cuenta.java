package com.mocatto.dto;

import java.io.Serializable;

/**
 * Created by froilan.ruiz on 6/6/2016.
 */
public class Cuenta implements Serializable {
    private String email;
    private String nombre;
    private String contrasenia;
    private String repetirContrasenia;
    private String telefono;
    private String pais;
    private String ciudad;

    private String fechaNacimiento;
    private String url;
    private String Ubicacion;
    private String locale;
    private String genero;

    @Override
    public String toString() {
        return "Cuenta{" +
                "email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", contrasenia='" + contrasenia + '\'' +
                ", repetirContrasenia='" + repetirContrasenia + '\'' +
                ", telefono='" + telefono + '\'' +
                ", pais='" + pais + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", url='" + url + '\'' +
                ", Ubicacion='" + Ubicacion + '\'' +
                ", locale='" + locale + '\'' +
                ", genero='" + genero + '\'' +
                '}';
    }

    public Cuenta(){}

    public Cuenta(String email, String nombre, String contrasenia, String repetirContrasenia, String telefono, String pais, String ciudad, String fechaNacimiento, String url, String ubicacion, String locale, String genero) {
        this.email = email;
        this.nombre = nombre;
        this.contrasenia = contrasenia;
        this.repetirContrasenia = repetirContrasenia;
        this.telefono = telefono;
        this.pais = pais;
        this.ciudad = ciudad;
        this.fechaNacimiento = fechaNacimiento;
        this.url = url;
        Ubicacion = ubicacion;
        this.locale = locale;
        this.genero = genero;
    }

    //Constructor con datos de facebook
    public Cuenta(String nombre, String email, String fechaNacimiento, String url, String ubicacion, String locale, String genero) {
        this.nombre = nombre;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.url = url;
        Ubicacion = ubicacion;
        this.locale = locale;
        this.genero = genero;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUbicacion() {
        return Ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        Ubicacion = ubicacion;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getRepetirContrasenia() {
        return repetirContrasenia;
    }

    public void setRepetirContrasenia(String repetirContrasenia) {
        this.repetirContrasenia = repetirContrasenia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}

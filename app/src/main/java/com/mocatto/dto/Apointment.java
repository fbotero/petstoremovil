package com.mocatto.dto;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by froilan.ruiz on 6/7/2016.
 */
public class Apointment implements Serializable {
    private Integer id;

    private String nombre;
    private String fecha;
    private String hora;
    private String ubicacion;
    private String recordarMinutosAntes;
    private String email;
    private String mascota;
    private String tipoEvento;
    private Integer estado;
    private Integer bathFrecuency;
    private Integer foodBuyRegularity;

    private Integer createdBy;

    protected Apointment(Parcel in) {
        nombre = in.readString();
        fecha = in.readString();
        hora = in.readString();
        ubicacion = in.readString();
        recordarMinutosAntes = in.readString();
        email = in.readString();
        mascota = in.readString();
        tipoEvento = in.readString();
        especie = in.readString();
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    private String especie;

    private Integer countReminder;

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getCountReminder() {
        return countReminder;
    }

    public void setCountReminder(Integer countReminder) {
        this.countReminder = countReminder;
    }

    public Apointment(){
    }

    public Apointment(Integer id,String nombre, String fecha, String hora, String ubicacion,
                      String recordarMinutosAntes, String email,String mascota, String tipoEvento,
                      String especie, Integer estado,Integer createdBy,Integer countReminder) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.ubicacion = ubicacion;
        this.recordarMinutosAntes = recordarMinutosAntes;
        this.email = email;
        this.mascota=mascota;
        this.tipoEvento=tipoEvento;
        this.especie = especie;
        this.estado = estado;
        this.createdBy = createdBy;
        this.countReminder = countReminder;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getRecordarMinutosAntes() {
        return recordarMinutosAntes;
    }

    public void setRecordarMinutosAntes(String recordarMinutosAntes) {
        this.recordarMinutosAntes = recordarMinutosAntes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMascota() {
        return mascota;
    }

    public void setMascota(String mascota) {
        this.mascota = mascota;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getBathFrecuency() {
        return bathFrecuency;
    }

    public void setBathFrecuency(Integer bathFrecuency) {
        this.bathFrecuency = bathFrecuency;
    }

    public Integer getFoodBuyRegularity() {
        return foodBuyRegularity;
    }

    public void setFoodBuyRegularity(Integer foodBuyRegularity) {
        this.foodBuyRegularity = foodBuyRegularity;
    }

    @Override
    public String toString() {
        return "Apointment{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", recordarMinutosAntes='" + recordarMinutosAntes + '\'' +
                ", email='" + email + '\'' +
                ", mascota='" + mascota + '\'' +
                ", tipoEvento='" + tipoEvento + '\'' +
                ", estado=" + estado +
                ", bathFrecuency=" + bathFrecuency +
                ", foodBuyRegularity=" + foodBuyRegularity +
                ", createdBy=" + createdBy +
                ", especie='" + especie + '\'' +
                ", countReminder=" + countReminder +
                '}';
    }
}

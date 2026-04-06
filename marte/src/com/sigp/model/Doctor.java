package com.sigp.model;

public class Doctor {
    private int id;
    private String nombreCompleto;
    private int edad;
    private String cedula;
    private String especialidad;

    public Doctor(String nombreCompleto, int edad, String cedula, String especialidad) {
        this.nombreCompleto = nombreCompleto;
        this.edad = edad;
        this.cedula = cedula;
        this.especialidad = especialidad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return "ID: " + id
                + " | Nombre: " + nombreCompleto
                + " | Edad: " + edad
                + " | Cedula: " + cedula
                + " | Especialidad: " + especialidad;
    }
}

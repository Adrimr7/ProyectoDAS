package com.example.das_primeraevaluacion;

public class Reserva {
    private int id;
    private String nombrePasajero;
    private String fechaReserva;
    private String avionNombre;

    // Constructora. Se usaran las reservas mas adelante.
    public Reserva(int id, String nombrePasajero, String avionNombre, String fechaReserva) {
        this.id = id;
        this.nombrePasajero = nombrePasajero;
        this.fechaReserva = fechaReserva;
        this.avionNombre = avionNombre;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombrePasajero() {
        return nombrePasajero;
    }

    public void setNombrePasajero(String nombrePasajero) {
        this.nombrePasajero = nombrePasajero;
    }

    public String getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(String fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getAvionNombre() {
        return avionNombre;
    }

    public void setAvionNombre(String avionNombre) {
        this.avionNombre = avionNombre;
    }
}

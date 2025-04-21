package com.example.das_primeraevaluacion;

public class Reserva {
    private int id;
    private String emailPasajero;
    private String fechaReserva;
    private String avionNombre;
    private Aeropuerto aeroOrigen;
    private Aeropuerto aeroDestino;

    public Reserva() {} // constructora vacia para el gson

    // constructora
    public Reserva(int id, String emailPasajero, String avionNombre, String fechaReserva, Aeropuerto aeroDestino, Aeropuerto aeroOrigen) {
        this.id = id;
        this.emailPasajero = emailPasajero;
        this.fechaReserva = fechaReserva;
        this.avionNombre = avionNombre;
        this.aeroDestino = aeroDestino;
        this.aeroOrigen = aeroOrigen;
    }

    // getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmailPasajero() {
        return emailPasajero;
    }

    public void setEmailPasajero(String emailPasajero) {
        this.emailPasajero = emailPasajero;
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

    public Aeropuerto getOrigen() {
        return this.aeroOrigen;
    }

    public Aeropuerto getDestino() {
        return this.aeroDestino;
    }

    public void setOrigen(Aeropuerto aero) {
        this.aeroOrigen = aero;
    }

    public void setDestino(Aeropuerto aero) {
        this.aeroDestino = aero;
    }
}

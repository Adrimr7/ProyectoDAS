package com.example.das_primeraevaluacion;

import java.util.List;

public class Avion{
    private int id;
    private String nombre;
    private String fabricante;
    private String modelo;
    private int alcanceKm;
    private int numPasajeros;
    private int personalCabina;
    private int tarifaBase;
    private String clase;
    private int tamanoM;
    private List<String> facilidades;

    // Constructor
    public Avion(int id, String nombre, String fabricante, String modelo, int alcanceKm, int numPasajeros,
                 int personalCabina, int tarifaBase, String clase, int tamanoM, List<String> facilidades) {
        this.id = id;
        this.nombre = nombre;
        this.fabricante = fabricante;
        this.modelo = modelo;
        this.alcanceKm = alcanceKm;
        this.numPasajeros = numPasajeros;
        this.personalCabina = personalCabina;
        this.tarifaBase = tarifaBase;
        this.clase = clase;
        this.tamanoM = tamanoM;
        this.facilidades = facilidades;
    }

    // Getters
    public int getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public String getFabricante() {
        return fabricante;
    }
    public String getModelo() {
        return modelo;
    }
    public int getAlcanceKm() {
        return alcanceKm;
    }
    public int getNumPasajeros() {
        return numPasajeros;
    }
    public int getPersonalCabina() {
        return personalCabina;
    }
    public int getTarifaBase() {
        return tarifaBase;
    }
    public String getClase() {
        return clase;
    }
    public int getTamanoM() {
        return tamanoM;
    }
    public List<String> getFacilidades() {
        return facilidades;
    }

    // Setters

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setModelo(String Modelo) {
        this.modelo = modelo;
    }
    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }
    public void setAlcanceKm(int alcanceKm) {
        this.alcanceKm = alcanceKm;
    }
    public void setNumPasajeros(int numPasajeros) {
        this.numPasajeros = numPasajeros;
    }
    public void setPersonalCabina(int personalCabina) {
        this.personalCabina = personalCabina;
    }
    public void setTarifaBase(int tarifaBase) {
        this.tarifaBase = tarifaBase;
    }
    public void setId(int id) {
        this.id = id;
    }
}

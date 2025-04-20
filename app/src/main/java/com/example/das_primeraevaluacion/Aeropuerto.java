package com.example.das_primeraevaluacion;

import java.io.Serializable;

public class Aeropuerto implements Serializable {

    private String codigo_icao;
    private String nombre;
    private double lat;
    private double lon;
    private String pais_iso;
    private String pais_ingles;
    private String pais_castellano;

    public Aeropuerto() {} // constructora vacia para el Gson

    public Aeropuerto(String nombre, String codigo_icao, double lat, double lon, String pais_castellano, String pais_ingles, String pais_iso) {
        this.nombre = nombre;
        this.codigo_icao = codigo_icao;
        this.lat = lat;
        this.lon = lon;
        this.pais_castellano = pais_castellano;
        this.pais_ingles = pais_ingles;
        this.pais_iso = pais_iso;
    }


    public String getCodigo_icao() {
        return codigo_icao;
    }
    public void setCodigo_icao(String codigo_icao) {
        this.codigo_icao = codigo_icao;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLon() {
        return lon;
    }
    public void setLon(double lon) {
        this.lon = lon;
    }
    public String getPais_iso() {
        return pais_iso;
    }
    public void setPais_iso(String pais_iso) {
        this.pais_iso = pais_iso;
    }
    public String getPais_ingles() {
        return pais_ingles;
    }
    public void setPais_ingles(String pais_ingles) {
        this.pais_ingles = pais_ingles;
    }
    public String getPais_castellano() {
        return pais_castellano;
    }
    public void setPais_castellano(String pais_castellano) {
        this.pais_castellano = pais_castellano;
    }
}
package com.example.andres.proyectofinal.Sitios;

public class Lugar {
    private int id;
    private String nombre;
    private String tipo;
    private String ubicacion;
    private String latitud;
    private String longitud;
    private String coment;
    private int evalua;

    public Lugar() {

    }

    public Lugar(int id, String nombre, String tipo, String ubicacion, String latitud, String longitud, String coment, int evalua) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ubicacion = ubicacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.coment = coment;
        this.evalua = evalua;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getComent() {
        return coment;
    }

    public void setComent(String coment) {
        this.coment = coment;
    }

    public int getEvalua() {
        return evalua;
    }

    public void setEvalua(int evalua) {
        this.evalua = evalua;
    }
}
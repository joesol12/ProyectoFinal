package com.example.andres.proyectofinal.Cliente;

public class Cliente {

    private String cedula;

    private String nombre;

    private String telefono;

    private String direccion;

    private String correo;

    public Cliente() {
    }

    public Cliente(String pCedula, String pNombre, String pTelefono, String pDireccion, String pCorreo) {
        this.cedula = pCedula;
        this.nombre = pNombre;
        this.telefono = pTelefono;
        this.direccion = pDireccion;
        this.correo = pCorreo;
    }

    public String toString() {
        return "Cliente{cedula='" + cedula + "\',nombre='" + nombre + "\',telefono='" + telefono + "\',direccion='" + direccion + "\',correo='" + correo + "\'}";
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String pCedula) {
        this.cedula = pCedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String pNombre) {
        this.nombre = pNombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String pTelefono) {
        this.telefono = pTelefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String pDireccion) {
        this.direccion = pDireccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String pCorreo) {
        this.correo = pCorreo;
    }
}

package com.example.usuario1.tarea5;

/**
 * Created by Usuario1 on 23/02/2018.
 */

public class Punto {
    private int id;
    private String nombre;
    private String coorx;
    private String coory;
    private String direccion;

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    private boolean visitado;

    public Punto() {

    }

    public Punto(String nombre, String coorx, String coory, boolean visitado) {
        this.nombre = nombre;
        this.coorx = coorx;
        this.coory = coory;
        this.visitado = visitado;
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

    public String getCoorx() {
        return coorx;
    }

    public void setCoorx(String coorx) {
        this.coorx = coorx;
    }

    public String getCoory() {
        return coory;
    }

    public void setCoory(String coory) {
        this.coory = coory;
    }

    public boolean isVisitado() {
        return visitado;
    }

    public void setVisitado(boolean visitado) {
        this.visitado = visitado;
    }

}

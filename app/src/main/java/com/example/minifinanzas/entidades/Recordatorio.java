package com.example.minifinanzas.entidades;

public class Recordatorio {

    private int Id;
    private String Nombre;
    private String Descripcion;
    private Double Cantidad;
    private String Fecha;
    private String Categoria;

    public Recordatorio(int Id, String Nombre, String Descripcion, Double Cantidad, String Fecha, String Categoria){
        this.Id = Id;
        this.Nombre = Nombre;
        this.Descripcion = Descripcion;
        this.Cantidad = Cantidad;
        this.Fecha = Fecha;
        this.Categoria = Categoria;
    }

    public Recordatorio (){

    }

    public int getId() {
        return Id;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public Double getCantidad() {
        return Cantidad;
    }

    public String getFecha() {
        return Fecha;
    }

    public String getCategoria() {
        return Categoria;
    }




    public void setId(int id) {
        Id = id;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public void setCantidad(Double cantidad) {
        Cantidad = cantidad;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }
}

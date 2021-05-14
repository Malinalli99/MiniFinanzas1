package com.example.minifinanzas.utilidades;

public class utilidades {

    public static final String TABLA_RECORDATORIOS = "Recordatorios";
    public static final String CAMPO_ID = "id";
    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_DESCRIPCION = "descripcion";
    public static final String CAMPO_CANTIDAD = "cantidad";
    public static final String CAMPO_FECHA = "fecha";
    public static final String CAMPO_CATEGORIA = "categoria";


    public static final String CREAR_TABLA_RECORDATORIOS ="CREATE TABLE "+TABLA_RECORDATORIOS+" ("+CAMPO_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CAMPO_NOMBRE+" TEXT, "+CAMPO_DESCRIPCION+" TEXT, "+CAMPO_CANTIDAD+" REAL, "+CAMPO_FECHA+" TEXT, "+CAMPO_CATEGORIA+" TEXT)";

    public static final String DROP_TABLA_RECORDATORIOS ="DROP TABLE IF EXISTS "+TABLA_RECORDATORIOS;

}

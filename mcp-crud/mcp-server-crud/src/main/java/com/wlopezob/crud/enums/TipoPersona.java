package com.wlopezob.crud.enums;

public enum TipoPersona {
    PADRE("Padre"),
    MADRE("Madre"),
    HIJO("Hijo");

    private final String descripcion;

    TipoPersona(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

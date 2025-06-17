package com.wlopezob.crud.exception;

public class DniDuplicadoException extends RuntimeException {
    public DniDuplicadoException(String dni) {
        super("El DNI ya existe: '" + dni + "'. Debe ser único.");
    }
} 
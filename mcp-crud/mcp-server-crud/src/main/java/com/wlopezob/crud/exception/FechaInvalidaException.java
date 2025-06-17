package com.wlopezob.crud.exception;

public class FechaInvalidaException extends RuntimeException {
    
    public FechaInvalidaException(String fecha) {
        super("Formato de fecha inválido: '" + fecha + "'. El formato debe ser: dd/MM/yyyy");
    }
    
    public FechaInvalidaException(String fecha, Throwable cause) {
        super("Formato de fecha inválido: '" + fecha + "'. El formato debe ser: dd/MM/yyyy", cause);
    }
}

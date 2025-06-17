package com.wlopezob.crud.exception;

public class TipoPersonaInvalidoException extends RuntimeException {
    
    public TipoPersonaInvalidoException(String tipoPersona) {
        super("Tipo de persona inválido: '" + tipoPersona + "'. Los valores permitidos son: PADRE, MADRE, HIJO");
    }
    
    public TipoPersonaInvalidoException(String tipoPersona, Throwable cause) {
        super("Tipo de persona inválido: '" + tipoPersona + "'. Los valores permitidos son: PADRE, MADRE, HIJO", cause);
    }
}

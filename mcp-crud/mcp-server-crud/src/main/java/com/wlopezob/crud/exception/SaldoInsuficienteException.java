package com.wlopezob.crud.exception;

public class SaldoInsuficienteException extends RuntimeException {
    
    public SaldoInsuficienteException(Long personaId, Double saldoActual, Double montoRequerido) {
        super("La persona con ID " + personaId + " tiene saldo insuficiente. Saldo actual: " + saldoActual + ", Monto requerido: " + montoRequerido);
    }
} 
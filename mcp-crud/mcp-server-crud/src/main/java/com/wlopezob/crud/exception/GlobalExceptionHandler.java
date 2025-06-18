package com.wlopezob.crud.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TipoPersonaInvalidoException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleTipoPersonaInvalido(TipoPersonaInvalidoException ex) {
        log.error("Error de tipo de persona inválido: {}", ex.getMessage());
        
        Map<String, Object> error = Map.of(
            "timestamp", LocalDateTime.now(),
            "status", HttpStatus.BAD_REQUEST.value(),
            "error", "Bad Request",
            "message", ex.getMessage(),
            "path", "/api/personas"
        );
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    @ExceptionHandler(FechaInvalidaException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleFechaInvalida(FechaInvalidaException ex) {
        log.error("Error de fecha inválida: {}", ex.getMessage());
        
        Map<String, Object> error = Map.of(
            "timestamp", LocalDateTime.now(),
            "status", HttpStatus.BAD_REQUEST.value(),
            "error", "Bad Request", 
            "message", ex.getMessage(),
            "path", "/api/personas"
        );
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    @ExceptionHandler(DniDuplicadoException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleDniDuplicado(DniDuplicadoException ex) {
        log.error("Error de DNI duplicado: {}", ex.getMessage());
        Map<String, Object> error = Map.of(
            "timestamp", LocalDateTime.now(),
            "status", HttpStatus.BAD_REQUEST.value(),
            "error", "Bad Request",
            "message", ex.getMessage(),
            "path", "/api/personas"
        );
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        log.error("Error de saldo insuficiente: {}", ex.getMessage());
        Map<String, Object> error = Map.of(
            "timestamp", LocalDateTime.now(),
            "status", HttpStatus.BAD_REQUEST.value(),
            "error", "Bad Request",
            "message", ex.getMessage(),
            "path", "/api/transactions"
        );
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        
        Map<String, Object> error = Map.of(
            "timestamp", LocalDateTime.now(),
            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error", "Internal Server Error",
            "message", "Ha ocurrido un error inesperado",
            "path", "/api/personas"
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }
}

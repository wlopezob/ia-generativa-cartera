package com.wlopezob.ux_api_restaurant.exception;

import com.wlopezob.ux_api_restaurant.dto.ChatResponse;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 * Manejador global de excepciones para la aplicación
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de argumentos inválidos
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ChatResponse>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Argumento inválido: {}", ex.getMessage());
        ChatResponse response = new ChatResponse("Error: " + ex.getMessage(), UUID.randomUUID().toString());
        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    /**
     * Maneja excepciones generales
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ChatResponse>> handleGenericException(Exception ex) {
        logger.error("Error interno del servidor", ex);
        ChatResponse response = new ChatResponse("Lo siento, ha ocurrido un error interno. Por favor, inténtalo de nuevo más tarde.", UUID.randomUUID().toString());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }
} 
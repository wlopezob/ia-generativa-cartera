package com.wlopezob.ux_api_restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO para solicitudes de chat con memoria de sesión
 * El sessionId es obligatorio y si no existe se creará automáticamente
 */
public record ChatWithMemoryRequest(
    @NotBlank(message = "El mensaje no puede estar vacío")
    String message,
    
    @NotNull(message = "El sessionId es obligatorio")
    UUID sessionId,
    
    String userId,

    String sessionName
) {} 
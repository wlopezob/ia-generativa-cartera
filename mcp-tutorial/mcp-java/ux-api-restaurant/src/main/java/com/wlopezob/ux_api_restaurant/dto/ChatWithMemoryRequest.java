package com.wlopezob.ux_api_restaurant.dto;

import java.util.UUID;

/**
 * DTO para solicitudes de chat con memoria de sesión
 */
public record ChatWithMemoryRequest(
    String message,
    UUID sessionId,
    String userId,
    String sessionName
) {} 
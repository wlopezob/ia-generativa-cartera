package com.wlopezob.ux_api_restaurant.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para respuestas de información de sesión
 */
public record SessionResponse(
    UUID sessionId,
    String sessionName,
    String userId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    int messageCount
) {} 
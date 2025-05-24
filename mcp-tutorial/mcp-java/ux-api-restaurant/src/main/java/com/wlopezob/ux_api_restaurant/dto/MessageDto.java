package com.wlopezob.ux_api_restaurant.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para representar mensajes de chat
 */
public record MessageDto(
    UUID id,
    String content,
    String role,
    LocalDateTime timestamp,
    UUID sessionId
) {} 
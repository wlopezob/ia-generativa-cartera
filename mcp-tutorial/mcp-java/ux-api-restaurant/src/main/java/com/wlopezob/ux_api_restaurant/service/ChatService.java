package com.wlopezob.ux_api_restaurant.service;

import com.wlopezob.ux_api_restaurant.dto.AdvancedChatRequest;
import com.wlopezob.ux_api_restaurant.dto.ChatRequest;
import com.wlopezob.ux_api_restaurant.dto.ChatResponse;
import reactor.core.publisher.Mono;

/**
 * Interface para el servicio de chat con IA
 */
public interface ChatService {

    /**
     * Procesa una solicitud de chat básica
     */
    Mono<ChatResponse> processChat(ChatRequest request);

    /**
     * Procesa una solicitud de chat avanzada con contexto adicional
     */
    Mono<ChatResponse> processAdvancedChat(AdvancedChatRequest request);
} 
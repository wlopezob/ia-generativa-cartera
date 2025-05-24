package com.wlopezob.ux_api_restaurant.service;

import com.wlopezob.ux_api_restaurant.dto.ChatWithMemoryRequest;
import com.wlopezob.ux_api_restaurant.dto.ChatResponse;
import com.wlopezob.ux_api_restaurant.dto.SessionResponse;
import com.wlopezob.ux_api_restaurant.entity.ChatMessage;
import com.wlopezob.ux_api_restaurant.entity.ChatSession;
import com.wlopezob.ux_api_restaurant.repository.ChatMessageRepository;
import com.wlopezob.ux_api_restaurant.repository.ChatSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Interface para el servicio de chat con memoria persistente
 */
public interface ChatMemoryService {

    /**
     * Procesa un chat con memoria de sesión
     */
    Mono<ChatResponse> processChatWithMemory(ChatWithMemoryRequest request);

    /**
     * Crea una nueva sesión de chat
     */
    Mono<SessionResponse> createSession(String userId, String sessionName);

    /**
     * Obtiene las sesiones de un usuario
     */
    Mono<List<SessionResponse>> getUserSessions(String userId);

    /**
     * Desactiva una sesión
     */
    Mono<Void> deactivateSession(UUID sessionId);

    /**
     * Elimina una sesión y todos sus mensajes
     */
    Mono<Void> deleteSession(UUID sessionId);
}
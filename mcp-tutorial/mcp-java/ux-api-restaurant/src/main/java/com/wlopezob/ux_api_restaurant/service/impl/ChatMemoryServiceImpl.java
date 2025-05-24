package com.wlopezob.ux_api_restaurant.service.impl;

import com.wlopezob.ux_api_restaurant.dto.ChatWithMemoryRequest;
import com.wlopezob.ux_api_restaurant.dto.ChatResponse;
import com.wlopezob.ux_api_restaurant.dto.SessionResponse;
import com.wlopezob.ux_api_restaurant.entity.ChatMessage;
import com.wlopezob.ux_api_restaurant.entity.ChatSession;
import com.wlopezob.ux_api_restaurant.repository.ChatMessageRepository;
import com.wlopezob.ux_api_restaurant.repository.ChatSessionRepository;
import com.wlopezob.ux_api_restaurant.service.ChatMemoryService;
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
 * Implementación del servicio de chat con memoria persistente usando PostgreSQL
 */
@Service
@Slf4j
@Transactional
public class ChatMemoryServiceImpl implements ChatMemoryService {

    private final ChatClient chatClient;
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private static final int MAX_CONTEXT_MESSAGES = 10;

    public ChatMemoryServiceImpl(ChatClient chatClient, 
                               ChatSessionRepository sessionRepository,
                               ChatMessageRepository messageRepository) {
        this.chatClient = chatClient;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * Procesa un chat con memoria de sesión
     */
    @Override
    @Transactional
    public Mono<ChatResponse> processChatWithMemory(ChatWithMemoryRequest request) {
        return Mono.fromCallable(() -> {
            log.info("Procesando chat con memoria para usuario: {}", request.userId());
            
            // Obtener o crear sesión
            ChatSession session = getOrCreateSession(request);
            
            // Guardar mensaje del usuario
            ChatMessage userMessage = ChatMessage.builder()
                    .chatSession(session)
                    .content(request.message())
                    .role(ChatMessage.MessageRole.USER)
                    .build();
            messageRepository.save(userMessage);
            
            // Obtener contexto de mensajes anteriores
            String contextualPrompt = buildContextualPrompt(session.getId(), request.message());
            
            // Generar respuesta
            String response = chatClient.prompt()
                    .user(contextualPrompt)
                    .call()
                    .content();
            
            // Guardar respuesta del asistente
            ChatMessage assistantMessage = ChatMessage.builder()
                    .chatSession(session)
                    .content(response)
                    .role(ChatMessage.MessageRole.ASSISTANT)
                    .build();
            messageRepository.save(assistantMessage);
            
            log.info("Chat procesado exitosamente para sesión: {}", session.getId());
            return new ChatResponse(response, session.getId().toString());
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Crea una nueva sesión de chat
     */
    @Override
    @Transactional
    public Mono<SessionResponse> createSession(String userId, String sessionName) {
        return Mono.fromCallable(() -> {
            log.info("Creando nueva sesión para usuario: {}", userId);
            
            ChatSession session = ChatSession.builder()
                    .userId(userId)
                    .sessionName(sessionName)
                    .isActive(true)
                    .build();
            
            ChatSession savedSession = sessionRepository.save(session);
            
            return new SessionResponse(
                savedSession.getId(),
                savedSession.getSessionName(),
                savedSession.getUserId(),
                savedSession.getCreatedAt(),
                savedSession.getUpdatedAt(),
                0
            );
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Obtiene las sesiones de un usuario
     */
    @Override
    @Transactional(readOnly = true)
    public Mono<List<SessionResponse>> getUserSessions(String userId) {
        return Mono.fromCallable(() -> {
            log.info("Obteniendo sesiones para usuario: {}", userId);
            
            List<ChatSession> sessions = sessionRepository.findByUserIdAndIsActiveTrueOrderByUpdatedAtDesc(userId);
            
            return sessions.stream()
                    .map(session -> {
                        long messageCount = messageRepository.countByChatSession_Id(session.getId());
                        return new SessionResponse(
                            session.getId(),
                            session.getSessionName(),
                            session.getUserId(),
                            session.getCreatedAt(),
                            session.getUpdatedAt(),
                            (int) messageCount
                        );
                    })
                    .collect(Collectors.toList());
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Desactiva una sesión
     */
    @Override
    @Transactional
    public Mono<Void> deactivateSession(UUID sessionId) {
        return Mono.fromRunnable(() -> {
            log.info("Desactivando sesión: {}", sessionId);
            
            sessionRepository.findById(sessionId)
                    .ifPresent(session -> {
                        session.setIsActive(false);
                        sessionRepository.save(session);
                    });
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    /**
     * Elimina una sesión y todos sus mensajes
     */
    @Override
    @Transactional
    public Mono<Void> deleteSession(UUID sessionId) {
        return Mono.fromRunnable(() -> {
            log.info("Eliminando sesión: {}", sessionId);
            
            messageRepository.deleteByChatSession_Id(sessionId);
            sessionRepository.deleteById(sessionId);
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    /**
     * Obtiene o crea una sesión según la solicitud
     */
    private ChatSession getOrCreateSession(ChatWithMemoryRequest request) {
        if (request.sessionId() != null) {
            return sessionRepository.findById(request.sessionId())
                    .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada: " + request.sessionId()));
        } else if (request.userId() != null && request.sessionName() != null) {
            ChatSession session = ChatSession.builder()
                    .userId(request.userId())
                    .sessionName(request.sessionName())
                    .isActive(true)
                    .build();
            return sessionRepository.save(session);
        } else {
            throw new IllegalArgumentException("Debe proporcionar sessionId o userId+sessionName");
        }
    }

    /**
     * Construye un prompt contextual con el historial de mensajes
     */
    private String buildContextualPrompt(UUID sessionId, String currentMessage) {
        List<ChatMessage> recentMessages = messageRepository.findRecentMessagesBySessionId(
                sessionId, 
                PageRequest.of(0, MAX_CONTEXT_MESSAGES)
        );
        
        // Invertir la lista para tener orden cronológico
        Collections.reverse(recentMessages);
        
        if (recentMessages.isEmpty()) {
            return currentMessage;
        }
        
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("Historial de conversación:\n");
        
        for (ChatMessage message : recentMessages) {
            contextBuilder.append(message.getRole().name())
                    .append(": ")
                    .append(message.getContent())
                    .append("\n");
        }
        
        contextBuilder.append("\nNuevo mensaje del usuario: ").append(currentMessage);
        contextBuilder.append("\n\nResponde considerando todo el contexto de la conversación.");
        
        return contextBuilder.toString();
    }
} 
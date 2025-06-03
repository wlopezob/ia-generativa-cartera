package com.wlopezob.ux_api_restaurant.service.impl;

import com.wlopezob.ux_api_restaurant.config.ChatConstants;
import com.wlopezob.ux_api_restaurant.dto.AdvancedChatRequest;
import com.wlopezob.ux_api_restaurant.dto.ChatRequest;
import com.wlopezob.ux_api_restaurant.dto.ChatResponse;
import com.wlopezob.ux_api_restaurant.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

/**
 * Implementación del servicio de chat con IA
 */
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    public ChatServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Procesa una solicitud de chat básica
     */
    @Override
    public Mono<ChatResponse> processChat(ChatRequest request) {
        return Mono.fromCallable(() -> {
            validateMessage(request.message());
            
            String response = chatClient.prompt()
                    .user(request.message())
                    .call()
                    .content();
                    
            return new ChatResponse(response, UUID.randomUUID().toString());
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Procesa una solicitud de chat avanzada con contexto adicional
     */
    @Override
    public Mono<ChatResponse> processAdvancedChat(AdvancedChatRequest request) {
        return Mono.fromCallable(() -> {
            validateMessage(request.message());
            
            var promptBuilder = chatClient.prompt()
                    .user(request.message());
            
            // Si se proporciona contexto adicional
            if (request.context() != null && !request.context().trim().isEmpty()) {
                String enhancedPrompt = ChatConstants.SYSTEM_PROMPT + "\n\nCONTEXTO ADICIONAL:\n" + request.context();
                promptBuilder = promptBuilder.system(enhancedPrompt);
            }
            
            String response = promptBuilder
                    .call()
                    .content();
                    
            return new ChatResponse(response, UUID.randomUUID().toString());
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Valida que el mensaje no esté vacío
     */
    private void validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacío");
        }
        if (message.length() > ChatConstants.MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("El mensaje no puede exceder " + ChatConstants.MAX_MESSAGE_LENGTH + " caracteres");
        }
    }
} 
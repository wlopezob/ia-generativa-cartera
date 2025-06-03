package com.wlopezob.ux_api_restaurant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración centralizada para el ChatClient
 */
@Configuration
public class ChatClientConfig {

    /**
     * Configura y proporciona un ChatClient completamente configurado
     * como bean singleton para toda la aplicación
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, 
                                ToolCallbackProvider toolCallbackProvider) {
        return chatClientBuilder
                .defaultSystem(ChatConstants.SYSTEM_PROMPT)
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }
} 
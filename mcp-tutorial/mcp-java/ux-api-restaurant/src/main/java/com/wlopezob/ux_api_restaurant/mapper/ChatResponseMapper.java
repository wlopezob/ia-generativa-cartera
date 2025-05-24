package com.wlopezob.ux_api_restaurant.mapper;

import com.wlopezob.ux_api_restaurant.dto.ChatResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.UUID;

/**
 * Mapper de MapStruct para transformaciones de ChatResponse
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatResponseMapper {

    /**
     * Crea un ChatResponse con respuesta y sessionId
     */
    default ChatResponse createChatResponse(String response, UUID sessionId) {
        return new ChatResponse(response, sessionId.toString());
    }

    /**
     * Crea un ChatResponse con respuesta y sessionId como String
     */
    default ChatResponse createChatResponse(String response, String sessionId) {
        return new ChatResponse(response, sessionId);
    }
} 
package com.wlopezob.ux_api_restaurant.mapper;

import com.wlopezob.ux_api_restaurant.dto.MessageDto;
import com.wlopezob.ux_api_restaurant.entity.ChatMessage;
import com.wlopezob.ux_api_restaurant.entity.ChatSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper de MapStruct para transformaciones de ChatMessage
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatMessageMapper {

    /**
     * Crea un ChatMessage de usuario
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "role", expression = "java(com.wlopezob.ux_api_restaurant.entity.ChatMessage.MessageRole.USER)")
    @Mapping(source = "session", target = "chatSession")
    @Mapping(source = "content", target = "content")
    ChatMessage createUserMessage(ChatSession session, String content);

    /**
     * Crea un ChatMessage de asistente
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "role", expression = "java(com.wlopezob.ux_api_restaurant.entity.ChatMessage.MessageRole.ASSISTANT)")
    @Mapping(source = "session", target = "chatSession")
    @Mapping(source = "content", target = "content")
    ChatMessage createAssistantMessage(ChatSession session, String content);

    /**
     * Crea un ChatMessage de sistema
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "role", expression = "java(com.wlopezob.ux_api_restaurant.entity.ChatMessage.MessageRole.SYSTEM)")
    @Mapping(source = "session", target = "chatSession")
    @Mapping(source = "content", target = "content")
    ChatMessage createSystemMessage(ChatSession session, String content);

    /**
     * Convierte ChatMessage a MessageDto
     */
    @Mapping(source = "chatSession.id", target = "sessionId")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    MessageDto toDto(ChatMessage message);

    /**
     * Convierte MessageRole enum a String
     */
    @org.mapstruct.Named("roleToString")
    default String roleToString(ChatMessage.MessageRole role) {
        return role != null ? role.name() : null;
    }
} 
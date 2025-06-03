package com.wlopezob.ux_api_restaurant.mapper;

import com.wlopezob.ux_api_restaurant.dto.ChatWithMemoryRequest;
import com.wlopezob.ux_api_restaurant.dto.SessionResponse;
import com.wlopezob.ux_api_restaurant.entity.ChatSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper de MapStruct para transformaciones de ChatSession
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatSessionMapper {

    /**
     * Convierte ChatWithMemoryRequest a ChatSession para crear nueva sesión
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    ChatSession toEntity(ChatWithMemoryRequest request);

    /**
     * Convierte ChatSession a SessionResponse
     */
    @Mapping(source = "id", target = "sessionId")
    @Mapping(target = "messageCount", ignore = true)
    SessionResponse toSessionResponse(ChatSession session);

    /**
     * Convierte ChatSession a SessionResponse con messageCount
     */
    @Mapping(source = "session.id", target = "sessionId")
    @Mapping(source = "messageCount", target = "messageCount")
    SessionResponse toSessionResponse(ChatSession session, int messageCount);
} 
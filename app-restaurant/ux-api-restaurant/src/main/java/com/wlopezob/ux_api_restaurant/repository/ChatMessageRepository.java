package com.wlopezob.ux_api_restaurant.repository;

import com.wlopezob.ux_api_restaurant.entity.ChatMessage;
import com.wlopezob.ux_api_restaurant.entity.ChatSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para gestionar mensajes de chat
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    /**
     * Encuentra mensajes por sesión ordenados por timestamp
     */
    List<ChatMessage> findByChatSessionOrderByTimestampAsc(ChatSession chatSession);

    /**
     * Encuentra mensajes por ID de sesión ordenados por timestamp
     */
    List<ChatMessage> findByChatSession_IdOrderByTimestampAsc(UUID sessionId);

    /**
     * Obtiene los últimos N mensajes de una sesión
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.chatSession.id = :sessionId ORDER BY m.timestamp DESC")
    List<ChatMessage> findRecentMessagesBySessionId(@Param("sessionId") UUID sessionId, Pageable pageable);

    /**
     * Cuenta mensajes por sesión
     */
    long countByChatSession_Id(UUID sessionId);

    /**
     * Elimina mensajes por sesión
     */
    void deleteByChatSession_Id(UUID sessionId);

    /**
     * Encuentra mensajes por contenido (búsqueda de texto)
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.chatSession.id = :sessionId AND LOWER(m.content) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<ChatMessage> findBySessionIdAndContentContaining(@Param("sessionId") UUID sessionId, 
                                                         @Param("searchText") String searchText);
} 
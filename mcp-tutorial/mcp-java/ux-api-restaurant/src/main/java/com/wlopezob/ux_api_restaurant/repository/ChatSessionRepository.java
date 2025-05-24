package com.wlopezob.ux_api_restaurant.repository;

import com.wlopezob.ux_api_restaurant.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para gestionar sesiones de chat
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    /**
     * Encuentra sesiones activas por ID de usuario
     */
    List<ChatSession> findByUserIdAndIsActiveTrueOrderByUpdatedAtDesc(String userId);

    /**
     * Encuentra todas las sesiones de un usuario (activas e inactivas)
     */
    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(String userId);

    /**
     * Cuenta el número de sesiones activas de un usuario
     */
    long countByUserIdAndIsActiveTrue(String userId);

    /**
     * Encuentra sesiones por nombre (búsqueda parcial)
     */
    @Query("SELECT s FROM ChatSession s WHERE s.userId = :userId AND s.sessionName LIKE %:sessionName% AND s.isActive = true")
    List<ChatSession> findByUserIdAndSessionNameContaining(@Param("userId") String userId, 
                                                          @Param("sessionName") String sessionName);
} 
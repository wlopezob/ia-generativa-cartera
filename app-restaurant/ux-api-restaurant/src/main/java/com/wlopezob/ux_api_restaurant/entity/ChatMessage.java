package com.wlopezob.ux_api_restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad para almacenar mensajes individuales de chat
 */
@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession chatSession;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageRole role;

    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;

    // Constructor personalizado para compatibilidad
    public ChatMessage(UUID sessionId, String content, String role) {
        this.content = content;
        this.role = MessageRole.valueOf(role.toUpperCase());
    }

    // Enum para roles de mensaje
    public enum MessageRole {
        USER, ASSISTANT, SYSTEM
    }
} 
package com.wlopezob.ux_api_restaurant.controller;

import com.wlopezob.ux_api_restaurant.dto.*;
import com.wlopezob.ux_api_restaurant.service.ApplicationInfoService;
import com.wlopezob.ux_api_restaurant.service.ChatMemoryService;
import com.wlopezob.ux_api_restaurant.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de restaurantes con IA
 */
@RestController
@RequestMapping("/api/restaurant")
@Slf4j
public class RestaurantController {

    private final ChatService chatService;
    private final ChatMemoryService chatMemoryService;
    private final ApplicationInfoService applicationInfoService;

    public RestaurantController(ChatService chatService,
            ChatMemoryService chatMemoryService,
            ApplicationInfoService applicationInfoService) {
        this.chatService = chatService;
        this.chatMemoryService = chatMemoryService;
        this.applicationInfoService = applicationInfoService;
    }

    /**
     * Endpoint para chat básico con IA
     */
    @PostMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("Procesando chat básico");
        return chatService.processChat(request);
    }

    /**
     * Endpoint para chat avanzado con contexto adicional
     */
    @PostMapping(value = "/chat/advanced", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ChatResponse> chatAdvanced(@RequestBody AdvancedChatRequest request) {
        log.info("Procesando chat avanzado");
        return chatService.processAdvancedChat(request);
    }

    /**
     * Endpoint para chat con memoria de sesión
     */
    @PostMapping(value = "/chat/memory", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ChatResponse> chatWithMemory(@RequestBody ChatWithMemoryRequest request) {
        log.info("Procesando chat con memoria para usuario: {}", request.userId());
        return chatMemoryService.processChatWithMemory(request);
    }

    /**
     * Endpoint para crear una nueva sesión de chat
     */
    @PostMapping(value = "/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SessionResponse> createSession(@RequestParam String userId,
            @RequestParam String sessionName) {
        log.info("Creando sesión para usuario: {} con nombre: {}", userId, sessionName);
        return chatMemoryService.createSession(userId, sessionName);
    }

    /**
     * Endpoint para obtener las sesiones de un usuario
     */
    @GetMapping(value = "/sessions/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<SessionResponse>> getUserSessions(@PathVariable String userId) {
        log.info("Obteniendo sesiones para usuario: {}", userId);
        return chatMemoryService.getUserSessions(userId);
    }

    /**
     * Endpoint para desactivar una sesión
     */
    @PutMapping(value = "/sessions/{sessionId}/deactivate", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> deactivateSession(@PathVariable UUID sessionId) {
        log.info("Desactivando sesión: {}", sessionId);
        return chatMemoryService.deactivateSession(sessionId);
    }

    /**
     * Endpoint para eliminar una sesión
     */
    @DeleteMapping(value = "/sessions/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> deleteSession(@PathVariable UUID sessionId) {
        log.info("Eliminando sesión: {}", sessionId);
        return chatMemoryService.deleteSession(sessionId);
    }

    /**
     * Endpoint de health check
     */
    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<HealthResponse> health() {
        return applicationInfoService.getHealth();
    }

    /**
     * Endpoint de información de la aplicación
     */
    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<InfoResponse> info() {
        return applicationInfoService.getInfo();
    }
}
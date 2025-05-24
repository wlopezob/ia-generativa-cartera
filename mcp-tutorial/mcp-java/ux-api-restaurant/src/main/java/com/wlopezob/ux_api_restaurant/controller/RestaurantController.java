package com.wlopezob.ux_api_restaurant.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private final ChatClient chatClient;
    private static final String SYSTEM_PROMPT = """
        Eres un asistente experto en gestión de restaurantes con acceso a herramientas especializadas.
        
        INSTRUCCIONES IMPORTANTES:
        1. Analiza cuidadosamente cada solicitud del usuario
        2. Puedes y DEBES usar múltiples herramientas si es necesario para completar la tarea
        3. Ejecuta las herramientas en el orden lógico correcto
        4. Si necesitas información de múltiples fuentes, no dudes en llamar varias herramientas
        5. Siempre proporciona respuestas completas y detalladas basadas en los datos obtenidos
        6. Siempre debes obtener los menús disponibles en el restaurante para poder responder las preguntas del usuario
        
        FLUJO DE TRABAJO:
        - Para consultas sobre menús: primero obtén el menú, luego los detalles si es necesario
        - Puedes combinar información de diferentes herramientas para dar respuestas más completas
        
        Responde siempre en español de manera profesional y amigable.
        """;

    public RestaurantController(ChatClient.Builder chatClientBuilder, 
                              ToolCallbackProvider toolCallbackProvider) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }

    @PostMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ChatResponse> chat(@RequestBody ChatRequest request) {
        return Mono.fromCallable(() -> {
            String response = chatClient.prompt()
                    .user(request.message())
                    .call()
                    .content();
                    
            return new ChatResponse(response);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping(value = "/chat/advanced", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ChatResponse> chatAdvanced(@RequestBody AdvancedChatRequest request) {
        return Mono.fromCallable(() -> {
            var promptBuilder = chatClient.prompt()
                    .user(request.message());
            
            // Si se proporciona contexto adicional
            if (request.context() != null && !request.context().isEmpty()) {
                promptBuilder = promptBuilder.system(SYSTEM_PROMPT + "\n\nCONTEXTO ADICIONAL:\n" + request.context());
            }
            
            String response = promptBuilder
                    .call()
                    .content();
                    
            return new ChatResponse(response);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<HealthResponse> health() {
        return Mono.just(new HealthResponse("OK", "Restaurant MCP Client is running"));
    }

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<InfoResponse> info() {
        return Mono.just(new InfoResponse(
            "UX API Restaurant", 
            "MCP Client for Restaurant Management with Multi-Tool Support",
            "1.0.0"
        ));
    }

    public record ChatRequest(String message) {}
    public record AdvancedChatRequest(String message, String context) {}
    public record ChatResponse(String response) {}
    public record HealthResponse(String status, String message) {}
    public record InfoResponse(String name, String description, String version) {}
} 
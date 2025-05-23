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

    public RestaurantController(ChatClient.Builder chatClientBuilder, 
                              ToolCallbackProvider toolCallbackProvider) {
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }

    @PostMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ChatResponse> chat(@RequestBody ChatRequest request) {
        return Mono.fromCallable(() -> {
            String response = chatClient.prompt(request.message()).call().content();
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
            "MCP Client for Restaurant Management",
            "1.0.0"
        ));
    }

    public record ChatRequest(String message) {}
    public record ChatResponse(String response) {}
    public record HealthResponse(String status, String message) {}
    public record InfoResponse(String name, String description, String version) {}
} 
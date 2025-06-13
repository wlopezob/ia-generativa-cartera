package com.wlopezob.mcp_kibana.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wlopezob.mcp_kibana.client.KibanaProxyClient;
import com.wlopezob.mcp_kibana.dto.*;
import com.wlopezob.mcp_kibana.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final KibanaProxyClient kibanaProxyClient;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<LogResponseDto> searchLogs(LogSearchRequestDto request) {
        return kibanaProxyClient.searchLogs(request)
                .map(this::parseLogResponse);
    }

    private LogResponseDto parseLogResponse(String rawResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            JsonNode hitsNode = rootNode.path("hits").path("hits");
            
            if (!hitsNode.isArray() || hitsNode.isEmpty()) {
                return new LogResponseDto(Collections.emptyList());
            }

            List<LogSourceDto> logs = new ArrayList<>();
            
            for (JsonNode hit : hitsNode) {
                JsonNode sourceNode = hit.path("_source");
                LogSourceDto source = parseLogSource(sourceNode);
                logs.add(source);
            }
            
            return new LogResponseDto(logs);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing log response", e);
        }
    }

    private LogSourceDto parseLogSource(JsonNode sourceNode) {
        return LogSourceDto.builder()
                .clazz(sourceNode.path("class").asText())
                .level(sourceNode.path("level").asText())
                .version(sourceNode.path("version").asText())
                .timestamp(sourceNode.path("@timestamp").asText())
                .httpMethod(sourceNode.path("httpMethod").asText())
                .env(sourceNode.path("env").asText())
                .message(sourceNode.path("message").asText())
                .pid(sourceNode.path("pid").asText())
                .thread(sourceNode.path("thread").asText())
                .serviceName(sourceNode.path("service-name").asText())
                .trace(sourceNode.path("trace").asText())
                .requestId(sourceNode.path("Request-ID").asText())
                .span(sourceNode.path("span").asText())
                .exportable(sourceNode.path("exportable").asText())
                .build();
    }
} 
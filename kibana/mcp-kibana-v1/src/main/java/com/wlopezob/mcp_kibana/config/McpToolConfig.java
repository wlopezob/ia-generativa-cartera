package com.wlopezob.mcp_kibana.config;

import java.util.List;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpServerFeatures.AsyncResourceSpecification;
import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;
import io.modelcontextprotocol.spec.McpSchema.Resource;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import io.modelcontextprotocol.spec.McpSchema.Annotations;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.logaritex.mcp.spring.SpringAiMcpAnnotationProvider;
import com.wlopezob.mcp_kibana.mcp.resources.DemoMcpResourceService;
import com.wlopezob.mcp_kibana.mcp.tools.IndexMcpToolService;
import com.wlopezob.mcp_kibana.mcp.tools.LogMcpToolService;

@Configuration
public class McpToolConfig {
    @Bean
    public ToolCallbackProvider mcpTools(IndexMcpToolService indexMcpToolService,
            LogMcpToolService logMcpToolService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(indexMcpToolService, logMcpToolService)
                .build();
    }

    @Bean
    public List<AsyncResourceSpecification> mcpResources(DemoMcpResourceService demoMcpResourceService) {
        /*
         * return SpringAiMcpAnnotationProvider
         * .createSyncResourceSpecifications(List.of(demoMcpResourceService));
         */
        var a = new McpServerFeatures.AsyncResourceSpecification(
                new Resource("file:///logs/user/{username}", "logs of user", "Get the logs about an user profile", "text/plain", null),
                (exchange, request) -> {
                    return Mono.fromSupplier(() -> "gola")
                    .map(res -> {
                        return new ReadResourceResult(List.of(new TextResourceContents(
                                "file:///logs/user/william",
                                "text/plain",
                                "nombre: william")));
                    });
                });
        return List.of(a);

    }
}
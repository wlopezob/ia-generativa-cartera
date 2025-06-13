package com.wlopezob.mcp_kibana.config;

import com.wlopezob.mcp_kibana.mcp.IndexMcpToolService;
import com.wlopezob.mcp_kibana.mcp.LogMcpToolService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolConfig {
    @Bean
    public ToolCallbackProvider mcpTools(IndexMcpToolService indexMcpToolService, 
                                         LogMcpToolService logMcpToolService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(indexMcpToolService, logMcpToolService)
                .build();
    }
} 
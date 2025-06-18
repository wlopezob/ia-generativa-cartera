package com.wlopezob.crud.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wlopezob.crud.mcp.tools.PersonaMcpTools;
import com.wlopezob.crud.mcp.tools.TransactionMcpTools;


@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider personaTools(PersonaMcpTools personaMcpTools, 
    TransactionMcpTools transactionMcpTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(personaMcpTools, transactionMcpTools)
                .build();
    }
} 
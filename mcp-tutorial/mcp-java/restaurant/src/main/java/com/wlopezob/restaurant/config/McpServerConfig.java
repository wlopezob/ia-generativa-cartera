package com.wlopezob.restaurant.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wlopezob.restaurant.toolservice.CulinaryStyleTool;
import com.wlopezob.restaurant.toolservice.MenuTool;
import com.wlopezob.restaurant.toolservice.RequestTool;

@Configuration
public class McpServerConfig {

    @Bean
	public ToolCallbackProvider mcpTools(
            CulinaryStyleTool culinaryStyleTool, 
            MenuTool menuTool,
            RequestTool requestTool) {
		return MethodToolCallbackProvider.builder()
            .toolObjects(culinaryStyleTool, menuTool, requestTool)
            .build();
	}

}

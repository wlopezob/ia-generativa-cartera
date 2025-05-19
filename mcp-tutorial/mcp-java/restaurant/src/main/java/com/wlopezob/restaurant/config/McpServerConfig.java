package com.wlopezob.restaurant.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wlopezob.restaurant.toolservice.CategoryTool;
import com.wlopezob.restaurant.toolservice.MenuTool;

@Configuration
public class McpServerConfig {

    @Bean
	public ToolCallbackProvider weatherTools(CategoryTool categoryTool, MenuTool menuTool) {
		return MethodToolCallbackProvider.builder().toolObjects(categoryTool, menuTool).build();
	}

}

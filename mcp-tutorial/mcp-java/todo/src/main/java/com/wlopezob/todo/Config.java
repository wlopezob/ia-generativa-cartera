package com.wlopezob.todo;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public ToolCallbackProvider todoTools(TodoAgentService todoAgentService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(todoAgentService).build();
    }

}

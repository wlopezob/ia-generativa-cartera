package com.wlopezob.llm.claude_ai;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.wlopezob.llm.claude_ai.domain.ClaudeAIService;

@SpringBootTest
public class ClaudeAITests {

  @Autowired
    private ClaudeAIService claudeAIService;

    @Test
    void testClaudeAIResponse() {
        String result = claudeAIService.chatWithClaude("¿Qué es Vaadin?");
        assertNotNull(result, "La respuesta no debe ser nula.");
        assertFalse(result.isEmpty(), "La respuesta no debe estar vacía.");
    }
}

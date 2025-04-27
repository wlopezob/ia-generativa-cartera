package com.wlopezob.llm.claude_ai.domain;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaudeAIService {

  private final AnthropicChatModel anthropicChatModel;

  public String chatWithClaude(String message) {
    Prompt prompt = new Prompt(message);
    ChatResponse response = anthropicChatModel.call(prompt);
    String rawResponse = response.getResult().getOutput().toString();

    return cleanResponse(rawResponse);
  }

  private String cleanResponse(String rawResponse) {
    String cleanedResponse = rawResponse;

    if(cleanedResponse.contains("AssistantMessage")) {
      int contentStart = cleanedResponse.indexOf("textContent=");
      if(contentStart >= 0) {
        cleanedResponse = cleanedResponse.substring(contentStart + 12);
      }
    }

    if(cleanedResponse.contains("metadata=")) {
      int metadataIndex = cleanedResponse.lastIndexOf(", metadata=");
      if(metadataIndex >= 0) {
        cleanedResponse = cleanedResponse.substring(0, metadataIndex);
      }
    }

    return cleanedResponse;
  }
}

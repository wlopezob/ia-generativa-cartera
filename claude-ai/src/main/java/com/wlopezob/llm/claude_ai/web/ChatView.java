package com.wlopezob.llm.claude_ai.web;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.wlopezob.llm.claude_ai.domain.ClaudeAIService;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Asistente con Claude AI")
public class ChatView extends VerticalLayout {

    private final ClaudeAIService claudeAIService;
    private final MessageList messageList = new MessageList();

    public ChatView(ClaudeAIService claudeAIService) {
        this.claudeAIService = claudeAIService;

        setSizeFull();
        setSpacing(false);
        setPadding(false);
        setAlignItems(Alignment.CENTER);

        H1 heading = new H1("Chat");
        heading.getStyle().set("margin-top", "var(--lumo-space-m)");

        Button newChatButton = new Button("New Chat");
        newChatButton.addClickListener(e -> messageList.setItems());
        newChatButton.getStyle().set("margin-bottom", "var(--lumo-space-m)");

        MessageInput input = getMessageInput(claudeAIService, messageList);

        Div container = new Div();
        container.setWidth("95%");
        container.setMaxWidth("900px");
        container.add(newChatButton, messageList, input);

        add(heading, container);
        expand(container);
    }

    private static MessageInput getMessageInput(ClaudeAIService claudeAIService, MessageList messageList) {
        MessageInput input = new MessageInput();
        input.addSubmitListener(submit -> {
            String userMessage = submit.getValue();
            MessageListItem userMsg = new MessageListItem(userMessage, Instant.now(), "Tú");
            userMsg.setUserColorIndex(1);

            String response = claudeAIService.chatWithClaude(userMessage);
            MessageListItem aiMsg = new MessageListItem(response, Instant.now(), "Claude AI");
            aiMsg.setUserColorIndex(2);

            List<MessageListItem> currentItems = new ArrayList<>(messageList.getItems());
            currentItems.add(userMsg);
            currentItems.add(aiMsg);
            messageList.setItems(currentItems);
        });
        return input;
    }
}

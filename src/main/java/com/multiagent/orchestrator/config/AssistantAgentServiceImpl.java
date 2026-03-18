package com.multiagent.orchestrator.config;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.List;

public class AssistantAgentServiceImpl implements AssistantAgentService {

    private final OpenAiChatModel chatModel;

    public AssistantAgentServiceImpl(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String chat(List<ChatMessage> messages) {
        ChatResponse response = chatModel.chat(messages);
        return response.aiMessage().text();
    }
}

package com.multiagent.orchestrator.config;

import dev.langchain4j.data.message.ChatMessage;

import java.util.List;

public interface AssistantAgentService {
    public String chat(List<ChatMessage> message);
}

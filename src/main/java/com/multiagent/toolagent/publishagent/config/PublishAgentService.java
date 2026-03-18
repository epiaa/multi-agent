package com.multiagent.toolagent.publishagent.config;

import com.multiagent.agent.AgentRequest;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.util.List;

public interface PublishAgentService {
    String chat(List<ChatMessage> message);
}

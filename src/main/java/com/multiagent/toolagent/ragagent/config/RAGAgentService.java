package com.multiagent.toolagent.ragagent.config;


import dev.langchain4j.data.message.ChatMessage;


import java.util.List;

public interface RAGAgentService {
    String chat(List<ChatMessage> message);
}

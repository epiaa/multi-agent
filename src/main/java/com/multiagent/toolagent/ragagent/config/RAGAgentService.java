package com.multiagent.toolagent.ragagent.config;

import com.multiagent.agent.AgentRequest;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;

public interface RAGAgentService {
    @SystemMessage(
            """
            你是rag-agent，完成它发布的任务  
            """
    )
    String chat(String message);
}

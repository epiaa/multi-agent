package com.multiagent.toolagent.publishagent.config;

import com.multiagent.agent.AgentRequest;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;

public interface PublishAgentService {
    @SystemMessage(
            """
            你是publish-agent，完成它发布的任务  
            """
    )
    String chat(String message);
}

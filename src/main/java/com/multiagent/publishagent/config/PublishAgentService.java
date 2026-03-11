package com.multiagent.publishagent.config;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;

public interface PublishAgentService {
    @SystemMessage(
            """
            你是publish-agent，接受supervisor-agent调度，
            完成它发布的任务  
            """
    )
    String chat(@MemoryId String memoryId, String task);
}

package com.multiagent.knowledgeagent.config;

import dev.langchain4j.service.SystemMessage;

public interface KnowledgeAgentService {
    @SystemMessage(
            """
            你是Knowledge-agent，接受supervisor-agent调度，
            完成它发布的任务  
            """
    )
    public String chat(String Task);
}

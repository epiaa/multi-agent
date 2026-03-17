package com.multiagent.agent;

import com.multiagent.toolagent.publishagent.config.PublishAgentService;
import com.multiagent.toolagent.ragagent.config.RAGAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class AgentFactory {

    public final Map<String, Agent> agents = new HashMap<>();

    public AgentFactory(
            RAGAgent ragAgent,
            PublishAgent publishAgent,
            AssistantAgent assistantAgent
    ) {
        agents.put("rag-agent", ragAgent);
        agents.put("publish-agent", publishAgent);
        agents.put("assistant-agent", assistantAgent);
    }

    public Agent getAgent(String agentName) {
        Agent a = agents.get(agentName);
        if (a == null) {
            throw new RuntimeException("Agent " + agentName + " not found");
        }
        return a;
    }

}

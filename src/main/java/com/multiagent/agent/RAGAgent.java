package com.multiagent.agent;

import com.multiagent.toolagent.ragagent.config.RAGAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class RAGAgent implements Agent {

    private final RAGAgentService ragAgentService;

    public RAGAgent(RAGAgentService ragAgentService) {
        this.ragAgentService = ragAgentService;
    }

    @Override
    public String name() {
        return "rag-agent";
    }

    @Override
    public boolean support(AgentRequest request) {
        return request.getMessage().contains("RAG");
    }

    @Override
    public AgentResponse execute(AgentRequest request) {
        String response = ragAgentService.chat(request.getMessage());
        return AgentResponse.builder()
                .requestId(request.getRequestId())
                .output(response)
                .handled(true)
                .build();
    }
}

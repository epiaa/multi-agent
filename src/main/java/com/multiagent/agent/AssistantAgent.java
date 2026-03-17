package com.multiagent.agent;

import com.multiagent.orchestrator.config.AssistantAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class AssistantAgent implements Agent {

    private final AssistantAgentService assistantAgentService;

    public AssistantAgent(AssistantAgentService assistantAgentService) {
        this.assistantAgentService = assistantAgentService;
    }

    @Override
    public String name() {
        return "assistant-agent";
    }

    @Override
    public boolean support(AgentRequest request) {
        return request.getMessage().contains("assistant");
    }

    @Override
    public AgentResponse execute(AgentRequest request) {
        String response = assistantAgentService.chat(request.getMessage());
        return AgentResponse.builder()
                .requestId(request.getRequestId())
                .output(response)
                .handled(true)
                .build();
    }
}

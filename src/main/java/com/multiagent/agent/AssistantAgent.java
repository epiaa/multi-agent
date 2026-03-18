package com.multiagent.agent;

import com.multiagent.orchestrator.config.AssistantAgentService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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

        String response = assistantAgentService.chat(request.getMessages());
        return AgentResponse.builder()
                .requestId(request.getRequestId())
                .output(response)
                .handled(true)
                .build();
    }
}

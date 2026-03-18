package com.multiagent.agent;

import com.multiagent.toolagent.publishagent.config.PublishAgentService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PublishAgent implements Agent {

    private final PublishAgentService publishAgentService;

    public PublishAgent(PublishAgentService publishAgentService) {
        this.publishAgentService = publishAgentService;
    }

    @Override
    public String name() {
        return "publish-agent";
    }

    @Override
    public boolean support(AgentRequest request) {
        return request.getMessage().contains("publish");
    }

    @Override
    public AgentResponse execute(AgentRequest request) {
        String response = publishAgentService.chat(request.getMessages());
        return AgentResponse.builder()
                .requestId(request.getRequestId())
                .output(response)
                .handled(true)
                .build();
    }
}

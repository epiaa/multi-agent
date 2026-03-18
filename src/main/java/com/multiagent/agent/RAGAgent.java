package com.multiagent.agent;

import com.multiagent.orchestrator.react.ReActState;
import com.multiagent.toolagent.ragagent.config.RAGAgentService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import lombok.RequiredArgsConstructor;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String response = ragAgentService.chat(request.getMessages());
        return AgentResponse.builder()
                .requestId(request.getRequestId())
                .output(response)
                .handled(true)
                .build();
    }
}

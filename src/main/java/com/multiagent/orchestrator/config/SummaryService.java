package com.multiagent.orchestrator.config;

import dev.langchain4j.data.message.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SummaryService {

    private final AssistantAgentService assistantAgentService;
    private final RedisChatMessageStore redisChatMessageStore;

    public void generateAndSaveSummary(Object o, List<ChatMessage> messages){
        String chat = assistantAgentService.chat(messages.toString());
        redisChatMessageStore.updateSummary(o, chat);
    }

}

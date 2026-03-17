package com.multiagent.orchestrator.config;

import com.multiagent.agent.AssistantAgent;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssistantAgentConfig {

    @Bean
    public AssistantAgentService assistantAgentService(
            OpenAiChatModel chatModel,
            ChatMemoryProvider chatMemoryProvider
    ) {
        return AiServices.builder(AssistantAgentService.class)
                .chatModel(chatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }
}

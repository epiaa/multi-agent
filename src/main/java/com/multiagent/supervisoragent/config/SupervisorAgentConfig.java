package com.multiagent.supervisoragent.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SupervisorAgentConfig {

    @Bean
    public SupervisorAgentService chatService(
            OpenAiChatModel chatModel,
            OpenAiStreamingChatModel streamingChatModel,
            ChatMemoryProvider chatMemoryProvider
    ){
        return AiServices.builder(SupervisorAgentService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }
}

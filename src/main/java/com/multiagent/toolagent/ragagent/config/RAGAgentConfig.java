package com.multiagent.toolagent.ragagent.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RAGAgentConfig {

    @Bean
    public RAGAgentService ragAgentService(
            OpenAiChatModel chatModel,
            ChatMemoryProvider chatMemoryProvider,
            ContentRetriever retriever
    ) {
        return AiServices.builder(RAGAgentService.class)
                .chatModel(chatModel)
                .contentRetriever(retriever)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }
}

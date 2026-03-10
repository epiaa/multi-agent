package com.multiagent.knowledgeagent.config;

import com.multiagent.agent.KnowledgeAgent;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KnowledgeAgentConfig {

    @Bean
    public KnowledgeAgentService knowledgeAgentService(
            OpenAiChatModel chatModel,
            ContentRetriever retriever
    ) {
        return AiServices.builder(KnowledgeAgentService.class)
                .chatModel(chatModel)
                .contentRetriever(retriever)
                .build();
    }
}

package com.multiagent.orchestrator.config;

import dev.langchain4j.community.model.dashscope.QwenTokenCountEstimator;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryProviderConfig {

    @Value("${langchain4j.open-ai.streaming-chat-model.api-key}")
    private String API_KEY;

    @Bean
    public ChatMemoryProvider chatMemoryProvider(RedisChatMessageStore store) {
        return o -> TokenWindowChatMemory.builder()
                .id(o)
                .maxTokens(2000, new QwenTokenCountEstimator(API_KEY, "qwen-turbo"))
                .chatMemoryStore(store)
                .build();
    }
}

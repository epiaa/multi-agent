package com.multiagent.supervisoragent.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatMemoryProviderConfig {

    private final ChatMemoryStore redisChatMemoryStore;

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return o -> MessageWindowChatMemory.builder()
                .id(o)
                .maxMessages(20)
                .chatMemoryStore(redisChatMemoryStore)
                .build();
    }
}

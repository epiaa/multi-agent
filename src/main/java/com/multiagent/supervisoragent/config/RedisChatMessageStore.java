package com.multiagent.supervisoragent.config;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisChatMessageStore implements ChatMemoryStore {
    private final StringRedisTemplate redisTemplate;

    @Override
    public List<ChatMessage> getMessages(Object o) {
        String json = redisTemplate.opsForValue().get(o.toString());
        return ChatMessageDeserializer.messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object o, List<ChatMessage> list) {
        String json = ChatMessageSerializer.messagesToJson(list);
        redisTemplate.opsForValue().set(o.toString(), json, Duration.ofDays(1));
    }

    @Override
    public void deleteMessages(Object o) {
        redisTemplate.delete(o.toString());
    }
}

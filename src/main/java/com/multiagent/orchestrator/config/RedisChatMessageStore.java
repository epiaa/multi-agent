package com.multiagent.orchestrator.config;

import dev.langchain4j.data.message.*;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisChatMessageStore implements ChatMemoryStore {
    private final StringRedisTemplate redisTemplate;

    @Override
    public List<ChatMessage> getMessages(Object o) {
        List<ChatMessage> all = new ArrayList<>();
        String summaryJson = getSummary(o);
        ChatMessage summary;
        if(summaryJson != null){
            summary = new SystemMessage(summaryJson);
            all.add(summary);
        }
        String messageJson = redisTemplate.opsForValue().get(getMessageKey(o));
        List<ChatMessage> messages = ChatMessageDeserializer.messagesFromJson(messageJson);
        all.addAll(messages);
        return all;
    }

    @Override
    public void updateMessages(Object o, List<ChatMessage> list) {
        String json = ChatMessageSerializer.messagesToJson(list);
        redisTemplate.opsForValue().set(getMessageKey(o), json, Duration.ofDays(1));
    }

    public void updateSummary(Object o, String message) {
        String oldSummary = getSummary(o);
        redisTemplate.opsForValue().set(getSummaryKey(o), oldSummary + '\n' + message);
    }

    public String getSummary(Object o) {
        return redisTemplate.opsForValue().get(getSummaryKey(o));
    }


    @Override
    public void deleteMessages(Object o) {
        redisTemplate.delete(getMessageKey(o));
        redisTemplate.delete(getSummaryKey(o));
    }

    private String getMessageKey(Object o) {
        String MESSAGE_KEY_SUFFIX = ":massage";
        return o.toString() + MESSAGE_KEY_SUFFIX;
    }

    private String getSummaryKey(Object o) {
        String SUMMARY_KEY_SUFFIX = ":summary";
        return o.toString() + SUMMARY_KEY_SUFFIX;
    }
}

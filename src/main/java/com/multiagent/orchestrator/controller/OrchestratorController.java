package com.multiagent.orchestrator.controller;

import com.multiagent.orchestrator.config.RedisChatMessageStore;
import dev.langchain4j.data.message.*;
import com.multiagent.agent.AgentRequest;
import com.multiagent.common.entity.UserHolder;
import com.multiagent.orchestrator.dispatcher.AgentDispatcher;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.service.MemoryId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/orchestrator")
@RequiredArgsConstructor
public class OrchestratorController {

    private final AgentDispatcher agentDispatcher;

    private final RedisChatMessageStore store;

    /**
     * 统一任务分发入口
     * 自动将用户消息路由到合适的专业Agent处理
     */
    @GetMapping("/dispatch")
    public String dispatch(String memoryId, String message) {
        Long userId = UserHolder.getUser().getId();
        List<ChatMessage> messages = store.getMessages(memoryId);
        log.info("original chat memory: {}", messages);
        ChatMessage msg = new UserMessage(message);
        messages.add(msg);
        log.info("new chat memory: {}", messages);
        AgentRequest request = AgentRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .userId(userId)
                .messages(ChatMessageSerializer.messagesToJson(messages))
                .build();
        String result = agentDispatcher.dispatch(request);
        messages.add(new AiMessage(result));
        store.updateMessages(memoryId, messages);
        return result;
    }

}

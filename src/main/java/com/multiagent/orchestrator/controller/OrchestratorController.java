package com.multiagent.orchestrator.controller;

import com.multiagent.agent.Agent;
import com.multiagent.agent.AgentRequest;
import com.multiagent.common.entity.UserHolder;
import com.multiagent.orchestrator.dispatcher.AgentDispatcher;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/orchestrator")
@RequiredArgsConstructor
public class OrchestratorController {

    private final AgentDispatcher agentDispatcher;

    /**
     * 统一任务分发入口
     * 自动将用户消息路由到合适的专业Agent处理
     */
    @GetMapping("/dispatch")
    public String dispatch(@MemoryId String memoryId, @UserMessage String message) {
        Long userId = UserHolder.getUser().getId();
        AgentRequest request = AgentRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .userId(userId)
                .memoryId(memoryId)
                .message(message)
                .build();
        return agentDispatcher.dispatch(request);
    }

}

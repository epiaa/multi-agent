package com.multiagent.supervisoragent.controller;

import com.multiagent.agent.dispatcher.AgentDispatcher;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor
public class SupervisorAgentController {

    private final AgentDispatcher agentDispatcher;

    /**
     * 统一任务分发入口
     * 自动将用户消息路由到合适的专业Agent处理
     */
    @GetMapping("/dispatch")
    public String dispatch(@MemoryId String memoryId, @UserMessage String message) {
        return agentDispatcher.dispatch(memoryId, message);
    }

}

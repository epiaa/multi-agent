package com.multiagent.agent;

import com.multiagent.supervisoragent.config.SupervisorAgentService;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupervisorAgent implements Agent {

    private final SupervisorAgentService supervisorAgentService;

    @Override
    public String name() {
        return "supervisor-agent";
    }

    @Override
    public String description() {
        return "调度Agent - 分析用户意图，将任务分发给合适的专业Agent处理";
    }

    @Override
    public String handle(String memoryId, String task) {
        return supervisorAgentService.chat(memoryId, task);
    }
}

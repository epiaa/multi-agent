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
        return "负责调度各agent工作，如果没有符合的agent的简单任务就自己解决";
    }

    @Override
    public String handle(String memoryId, String task) {
        return supervisorAgentService.chat(memoryId, task);
    }
}

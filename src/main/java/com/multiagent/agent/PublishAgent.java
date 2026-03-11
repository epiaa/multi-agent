package com.multiagent.agent;

import com.multiagent.publishagent.config.PublishAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublishAgent implements Agent {

    private final PublishAgentService publishAgentService;

    @Override
    public String name() {
        return "publish-agent";
    }

    @Override
    public String description() {
        return "课程订阅Agent - 管理用户的课程订阅，执行订阅/取消订阅/查看课程等操作";
    }

    @Override
    public String handle(String memoryId, String task) {
        return publishAgentService.chat(memoryId, task);
    }
}

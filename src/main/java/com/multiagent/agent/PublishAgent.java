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
        return "操作publish数据库，完成订阅课程相关的问题";
    }

    @Override
    public String handle(String memoryId, String task) {
        return publishAgentService.chat(task);
    }
}

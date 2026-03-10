package com.multiagent.supervisoragent.config;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface SupervisorAgentService {

    @SystemMessage(
            """
            你的职责是负责agent调度，
            你有两个tool agent：
                1. publish-agent：它负责课程订阅，如果涉及到，就把任务交给它。
                2. knowledge-agent：它负责知识增强检索，如果你有不知道的东西，就把任务交给他。
            你返回JSON格式,
            示例：
            {
                "agent": "knowledge | publish",
                "task":  "解释java线程池"
            }                  
            """
    )
    String chat(@MemoryId String id, @UserMessage String message);
}

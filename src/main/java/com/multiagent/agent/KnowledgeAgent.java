package com.multiagent.agent;

import com.multiagent.knowledgeagent.config.KnowledgeAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KnowledgeAgent implements Agent {

    private final KnowledgeAgentService knowledgeAgentService;

    @Override
    public String name() {
        return "knowledge-agent";
    }

    @Override
    public String description() {
        return "知识问答Agent - 回答课程相关的知识问题，提供技术讲解和知识检索";
    }

    @Override
    public String handle(String memoryId, String task) {
        return knowledgeAgentService.chat(memoryId, task);
    }
}

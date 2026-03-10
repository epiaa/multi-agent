package com.multiagent.supervisoragent.controller;

import com.multiagent.knowledgeagent.config.KnowledgeAgentService;
import com.multiagent.publishagent.config.PublishAgentService;
import com.multiagent.supervisoragent.config.SupervisorAgentService;
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

    private final SupervisorAgentService supervisorAgentService;
    private final KnowledgeAgentService knowledgeAgentService;
    private final PublishAgentService publishAgentService;


    @GetMapping("/dispatch")
    public String dispatch(@MemoryId String memoryId, @UserMessage String message) {
        String chat = supervisorAgentService.chat(memoryId, message);
        if (chat.contains("knowledge")) {
            return knowledgeAgentService.chat(message);
        }
        else {
            return publishAgentService.chat(message);
        }
    }

}

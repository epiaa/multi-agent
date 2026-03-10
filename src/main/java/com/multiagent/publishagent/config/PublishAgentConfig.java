package com.multiagent.publishagent.config;


import com.multiagent.agent.PublishAgent;
import com.multiagent.publishagent.tool.PublishAgentTool;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PublishAgentConfig {

    @Bean
    public PublishAgentService publishAgentService(
            OpenAiChatModel chatModel,
            PublishAgentTool publishAgentTool
    ) {
        return AiServices.builder(PublishAgentService.class)
                .chatModel(chatModel)
                .tools(publishAgentTool)
                .build();
    }
}

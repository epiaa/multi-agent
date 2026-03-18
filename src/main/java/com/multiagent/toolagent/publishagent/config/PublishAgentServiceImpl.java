package com.multiagent.toolagent.publishagent.config;

import com.multiagent.toolagent.publishagent.tool.PublishAgentTool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.service.tool.ToolExecutor;


import java.util.ArrayList;
import java.util.List;


public class PublishAgentServiceImpl implements PublishAgentService {

    private final OpenAiChatModel chatModel;

    private final PublishAgentTool publishAgentTool;

    public PublishAgentServiceImpl(OpenAiChatModel chatModel, PublishAgentTool publishAgentTool) {
        this.chatModel = chatModel;
        this.publishAgentTool = publishAgentTool;
    }

    @Override
    public String chat(List<ChatMessage> message) {
        ChatRequest chatRequest = ChatRequest.builder()
                .messages(message)
                .toolSpecifications(ToolSpecifications.toolSpecificationsFrom(publishAgentTool))
                .build();
        ChatResponse response = chatModel.chat(chatRequest);
        AiMessage aiMessage = response.aiMessage();

        // 👉 1. 如果没有 tool 调用，直接返回
        if (aiMessage.toolExecutionRequests() == null ||
                aiMessage.toolExecutionRequests().isEmpty()) {

            return aiMessage.text();
        }

        // 👉 2. 执行 tool
        List<ToolExecutionResultMessage> results = new ArrayList<>();

        for (ToolExecutionRequest req : aiMessage.toolExecutionRequests()) {
            ToolExecutor toolExecutor = new DefaultToolExecutor(publishAgentTool, req);
            String result = toolExecutor.execute(req, publishAgentTool);
            if (result == null) {
                throw new RuntimeException("Tool返回null，检查实现");
            }

            results.add(ToolExecutionResultMessage.from(
                    req,
                    result
            ));
        }

        // 👉 3. 回填消息
        List<ChatMessage> newMessages = new ArrayList<>();
        newMessages.add(aiMessage);
        newMessages.addAll(results);

        // 👉 4. 再调用 LLM（关键！）
        ChatResponse finalResponse = chatModel.chat(
                ChatRequest.builder()
                        .messages(newMessages)
                        .toolSpecifications(ToolSpecifications.toolSpecificationsFrom(publishAgentTool))
                        .build()
        );

        return finalResponse.aiMessage().text();
    }
}

package com.multiagent.toolagent.ragagent.config;


import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.ContentMetadata;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RAGAgentServiceImpl implements RAGAgentService {

    public final OpenAiChatModel chatModel;

    public final ContentRetriever retriever;

    public RAGAgentServiceImpl(OpenAiChatModel chatModel,
                               ContentRetriever contentRetriever) {
        this.chatModel = chatModel;
        this.retriever = contentRetriever;
    }

    @Override
    public String chat(List<ChatMessage> messages) {

        ChatMessage lastMessage = messages.get(messages.size() - 1);

        if (!(lastMessage instanceof UserMessage userMessage)) {
            throw new IllegalArgumentException("最后一条必须是用户消息");
        }

        String question = userMessage.singleText();

        Query query = Query.from(question);

        List<Content> contents = retriever.retrieve(query);


        String context = contents.stream()
                .limit(5)
                .map(content -> {
                    TextSegment segment = content.textSegment();
                    Metadata metadata = content.textSegment().metadata();

                    String source = metadata.getString("source");

                    return """
                        【来源】%s
                        【内容】%s
                        """.formatted(
                            source != null ? source : "未知",
                            segment.text()
                    );
                })
                .collect(Collectors.joining("\n\n"));

        String enhancedPrompt = buildPrompt(context, question);

        List<ChatMessage> newMessages = new ArrayList<>(messages);
        newMessages.set(newMessages.size() - 1, new UserMessage(enhancedPrompt));

        ChatResponse response = chatModel.chat(newMessages);

        return response.aiMessage().text();
    }

    private String buildPrompt(String context, String question) {
        return """
            你是一个专业助手，请基于提供的上下文回答问题。

            【上下文】
            %s

            【问题】
            %s

            要求：
            1. 优先使用上下文回答
            2. 如果上下文不足，请说明“不确定”
            3. 不要编造信息
            """.formatted(context, question);
    }

}

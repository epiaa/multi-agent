package com.multiagent.agent;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentRequest implements Serializable {
    private static final long serialVersionUID = 10001L;
    private String requestId;
    private Long userId;
    private String memoryId;
    private String message;
    private String messages;

    public List<ChatMessage> getMessages() {
        return ChatMessageDeserializer.messagesFromJson(messages);
    }

}

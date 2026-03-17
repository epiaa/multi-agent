package com.multiagent.agent;

import lombok.Builder;
import lombok.Data;

import java.util.Map;


@Data
@Builder
public class AgentRequest {
    private String requestId;
    private Long userId;
    private String memoryId;
    private String message;
}

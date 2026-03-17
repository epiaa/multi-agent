package com.multiagent.agent;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentResponse {
    private String requestId;
    private String output;
    private boolean handled;
}

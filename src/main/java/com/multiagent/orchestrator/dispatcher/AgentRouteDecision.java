package com.multiagent.orchestrator.dispatcher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentRouteDecision {
    private String agent;
    private String task;
    private String reason;
}

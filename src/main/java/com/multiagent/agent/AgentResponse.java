package com.multiagent.agent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse implements Serializable {
    private static final long serialVersionUID = 10002L;
    private String requestId;
    private String output;
    private boolean handled;
}

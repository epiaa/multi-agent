package com.multiagent.agent;

public interface Agent {

    String name();

    boolean support(AgentRequest agentRequest);

    AgentResponse execute(AgentRequest agentRequest);
}

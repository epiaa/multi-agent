package com.multiagent.agent;

public interface Agent {

    String name();

    String description();

    String handle(String memoryId, String task);
}

package com.multiagent.orchestrator.react;

import com.multiagent.agent.AgentRequest;
import lombok.*;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.io.Serializable;
import java.util.*;

public class ReActState extends AgentState {

    // ===== KEY =====
    public static final String HISTORY_KEY = "history";
    public static final String THOUGHT_KEY = "thought";
    public static final String ACTION_KEY = "action";
    public static final String OBSERVATION_KEY = "observation";
    public static final String ITERATIONS_KEY = "iterations";
    public static final String FINISHED_KEY = "finished";
    public static final String ANSWER_KEY = "finalAnswer";
    public static final String ORIGINAL_REQUEST_KEY = "originalRequest";


    // ===== SCHEMA =====
    public static final Map<String, Channel<?>> SCHEMA = Map.ofEntries(
            Map.entry(HISTORY_KEY, Channels.appender(ArrayList::new)),
            Map.entry(THOUGHT_KEY, Channels.base((o, n) -> n)),
            Map.entry(ACTION_KEY, Channels.base((o, n) -> n)),
            Map.entry(OBSERVATION_KEY, Channels.base((o, n) -> n)),
            Map.entry(ITERATIONS_KEY, Channels.base((o, n) -> n)),
            Map.entry(FINISHED_KEY, Channels.base((o, n) -> n)),
            Map.entry(ANSWER_KEY, Channels.base((o, n) -> n)),
            Map.entry(ORIGINAL_REQUEST_KEY, Channels.base(AgentRequest::new))
    );

    public ReActState(Map<String, Object> initData) {
        super(initData);
    }

    public List<StepRecord> getHistory() {

        return this.<List<StepRecord>>value(HISTORY_KEY)
                .orElseGet(ArrayList::new);
    }

    public AgentAction getAction() {
        return this.<AgentAction>value(ACTION_KEY)
                .orElseGet(AgentAction::new);
    }

    public String getObservation() {
        return this.<String>value(OBSERVATION_KEY).orElse(null);
    }

    public String getThought() {
        return this.<String>value(THOUGHT_KEY).orElse(null);
    }

    public int getIterations() {
        return this.<Integer>value(ITERATIONS_KEY).orElse(0);
    }

    public AgentRequest getOriginalRequest() {
        return this.<AgentRequest>value(ORIGINAL_REQUEST_KEY).orElseGet(AgentRequest::new);
    }

    public boolean isFinished() {
        return this.<Boolean>value(FINISHED_KEY).orElse(false);
    }

    public String getFinalAnswer() {
        return this.<String>value(ANSWER_KEY).orElse(null);
    }


    // =========================
    // 内部类
    // =========================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgentAction implements Serializable {
        private static final long serialVersionUID = 101L;
        private String agent;
        private String task;
        private String reason;
    }

    @Data
    public static class StepRecord implements Serializable {
        private static final long serialVersionUID = 102L;
        private final String thought;
        private final AgentAction action;
        private final String observation;

        public StepRecord(String thought, AgentAction action, String observation) {
            this.thought = thought;
            this.action = action;
            this.observation = observation;
        }
    }
}
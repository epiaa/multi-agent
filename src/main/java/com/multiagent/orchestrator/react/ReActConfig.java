package com.multiagent.orchestrator.react;

import com.multiagent.orchestrator.node.ActNode;
import com.multiagent.orchestrator.node.ObserveNode;
import com.multiagent.orchestrator.node.ThinkNode;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.CompiledGraph;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Bean;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;


import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Configuration
public class ReActConfig {

    @Bean
    public CompiledGraph<ReActState> ReActGraph(
            ThinkNode thinkNode,
            ActNode actNode,
            ObserveNode observeNode
    ) throws GraphStateException {
        return new StateGraph<>(ReActState.SCHEMA, ReActState::new)
                .addNode("think", node_async(thinkNode))
                .addNode("act", node_async(actNode))
                .addNode("observe", node_async(observeNode))
                .addEdge(START, "think")
                .addConditionalEdges(
                        "think",
                        this::shouldContinue,
                        Map.of(
                                "continue", "act",
                                "end", END
                        )
                )
                .addEdge("act", "observe")
                .addConditionalEdges(
                        "observe",
                        this::shouldContinueAfterObserve,
                        Map.of(
                                "continue", "think",
                                "end", END
                        )
                )
                .compile();

    }

    /**
     * 判断是否继续执行
     */
    private CompletableFuture<String> shouldContinue(ReActState state) {
        if (state.isFinished() || state.getFinalAnswer().contains("Final Answer")) {
            return CompletableFuture.completedFuture("end");
        }
        return CompletableFuture.completedFuture("continue");
    }

    /**
     * 观察后判断是否继续
     */
    private CompletableFuture<String> shouldContinueAfterObserve(ReActState state) {
        // 如果已完成，结束
        if (state.isFinished() || state.getFinalAnswer().contains("Final Answer")) {
            return CompletableFuture.completedFuture("end");
        }
        return CompletableFuture.completedFuture("continue");
    }

}

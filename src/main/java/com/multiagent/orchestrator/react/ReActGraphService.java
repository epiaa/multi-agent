package com.multiagent.orchestrator.react;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multiagent.agent.Agent;
import com.multiagent.agent.AgentFactory;
import com.multiagent.agent.AgentRequest;
import com.multiagent.agent.AgentResponse;
import com.multiagent.orchestrator.dispatcher.AgentRouteDecision;
import com.multiagent.orchestrator.prompt.ReActPrompt;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * ReAct循环图
 * 实现 思考 -> 执行 -> 观察 -> 再思考 的循环
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReActGraphService {

    public final CompiledGraph<ReActState> graph;
    
    /**
     * 执行ReAct循环
     */
    public String execute(AgentRequest request) {
        try {
            graph.setMaxIterations(10);
            ReActState finalState = graph.invoke(
                    Map.of(
                            ReActState.ORIGINAL_REQUEST_KEY, request
                    )
            ).get();

            if (finalState.getFinalAnswer() != null) {
                return finalState.getFinalAnswer();
            }
            
            // 如果没有最终答案，返回最后一次观察结果
            if (finalState.getObservation() != null) {
                return finalState.getObservation();
            }
            
            return "无法处理您的请求";
        } catch (Exception e) {
            log.error("ReAct execution failed", e);
            throw new RuntimeException("ReAct execution failed", e);
        }
    }
}

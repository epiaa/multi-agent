package com.multiagent.orchestrator.node;

import com.multiagent.agent.Agent;
import com.multiagent.agent.AgentFactory;
import com.multiagent.agent.AgentRequest;
import com.multiagent.agent.AgentResponse;
import com.multiagent.common.entity.UserHolder;
import com.multiagent.orchestrator.react.ReActState;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.multiagent.orchestrator.react.ReActState.ITERATIONS_KEY;
import static com.multiagent.orchestrator.react.ReActState.OBSERVATION_KEY;

/**
 * 执行节点
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActNode implements NodeAction<ReActState> {

    public final AgentFactory agentFactory;

    @Override
    public Map<String, Object> apply(ReActState state) throws Exception {
        log.info("=== ReAct Act ===");

        ReActState.AgentAction action = state.getAction();
        if (action == null || action.getAgent() == null || action.getTask() == null) {
            log.warn("No action to execute");
            return Map.of("observation", "No action to execute");
        }

        String agentName = action.getAgent();
        String task = action.getTask();

        log.info("执行Agent: {}, 任务: {}", agentName, task);

        Agent agent = agentFactory.getAgent(agentName);
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new UserMessage(task));
        AgentRequest request = AgentRequest.builder()
                .messages(ChatMessageSerializer.messagesToJson(messages))
                .userId(state.getOriginalRequest().getUserId())
                .memoryId(state.getOriginalRequest().getMemoryId())
                .requestId(state.getOriginalRequest().getRequestId())
                .build();

        AgentResponse response = agent.execute(request);
        String result = response.getOutput();

        log.info("Agent执行结果: {}", result);

        return Map.of(
                OBSERVATION_KEY, result,
                ITERATIONS_KEY, state.getIterations() + 1
        );
    }
}

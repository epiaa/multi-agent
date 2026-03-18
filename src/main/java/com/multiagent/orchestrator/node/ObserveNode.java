package com.multiagent.orchestrator.node;

import com.alibaba.dashscope.common.History;
import com.multiagent.orchestrator.react.ReActState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.multiagent.orchestrator.react.ReActState.*;

/**
 * 观察节点
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ObserveNode implements NodeAction<ReActState> {

    @Override
    public Map<String, Object> apply(ReActState state) throws Exception {
        log.info("=== ReAct Observe ===");

        String observation = state.getObservation();
        String thought = state.getThought();
        ReActState.AgentAction action = state.getAction();

        log.info("观察结果: {}", observation);
        log.info("当前迭代次数: {}", state.getIterations());

        return Map.of(
                HISTORY_KEY, new ReActState.StepRecord(thought, action, observation),
                ITERATIONS_KEY, state.getIterations() + 1
        );
    }
}

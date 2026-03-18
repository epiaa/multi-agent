package com.multiagent.orchestrator.dispatcher;

import com.multiagent.agent.AgentRequest;
import com.multiagent.orchestrator.react.ReActGraphService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Agent调度器
 * 使用ReAct模式（思考->执行->观察->再思考）进行任务分发
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AgentDispatcher {

    private final ReActGraphService reActGraph;

    /**
     * 使用ReAct循环分发任务
     * 实现 思考 -> 执行 -> 观察 -> 再思考 的循环
     */
    public String dispatch(AgentRequest request) {
        log.info("开始ReAct循环处理请求: {}", request.getMessages());
        return reActGraph.execute(request);
    }
}

package com.multiagent.orchestrator.node;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multiagent.agent.Agent;
import com.multiagent.agent.AgentFactory;
import com.multiagent.agent.AgentRequest;
import com.multiagent.agent.AgentResponse;
import com.multiagent.orchestrator.dispatcher.AgentRouteDecision;
import com.multiagent.orchestrator.prompt.ReActPrompt;
import com.multiagent.orchestrator.react.ReActState;
import dev.langchain4j.data.message.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.multiagent.orchestrator.react.ReActState.*;

/**
 * 思考节点
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ThinkNode implements NodeAction<ReActState> {

    public final AgentFactory agentFactory;
    public final ObjectMapper objectMapper;

    @Override
    public Map<String, Object> apply(ReActState state) throws Exception {
        log.info("=== ReAct Think (iteration {}) ===", state.getIterations());

        Agent assistant = agentFactory.getAgent("assistant-agent");

        // 构建包含历史的消息
        String context = buildContext(state);

        List<ChatMessage> messages = new ArrayList<>();

        SystemMessage systemMessage = new SystemMessage(ReActPrompt.REACT_PROMPT);
        messages.add(systemMessage);

        messages.addAll(state.getOriginalRequest().getMessages());

        if (!context.isEmpty() && !context.isBlank()) {
            AiMessage aiMessage = new AiMessage(context);
            messages.add(aiMessage);
        }
        AgentRequest request = AgentRequest.builder()
                .messages(ChatMessageSerializer.messagesToJson(messages))
                .userId(state.getOriginalRequest().getUserId())
                .memoryId(state.getOriginalRequest().getMemoryId())
                .requestId(state.getOriginalRequest().getRequestId())
                .build();

        AgentResponse response = assistant.execute(request);
        String llmOutput = response.getOutput();
        log.info("LLM思考输出: {}", llmOutput);

        // 解析思考结果
        ReActState.AgentAction action = parseThinkOutput(llmOutput, state);
        String thought = extractThought(llmOutput);

        boolean finished = false;
        String finalAnswer = "";

        // 检查是否有最终答案
        if (llmOutput.contains("Final Answer:")) {
            finalAnswer = extractFinalAnswer(llmOutput);
            finished = true;
        }

        return Map.of(
                THOUGHT_KEY, thought,
                ACTION_KEY, action,
                FINISHED_KEY, finished,
                ANSWER_KEY, finalAnswer,
                ITERATIONS_KEY, state.getIterations() + 1
        );
    }

    /**
     * 构建上下文，包含历史记录
     */
    private String buildContext(ReActState state) {
        StringBuilder context = new StringBuilder();

        // 添加历史记录
        if (!state.getHistory().isEmpty()) {
            context.append("历史记录:\n");
            for (ReActState.StepRecord record : state.getHistory()) {
                context.append("Thought: ").append(record.getThought()).append("\n");
                if (record.getAction() != null) {
                    context.append("Action: 调用 ").append(record.getAction().getAgent()).append("\n");
                }
                context.append("Observation: ").append(record.getObservation()).append("\n\n");
            }
        }

        return context.toString();
    }

    private static final Pattern JSON_PATTERN = Pattern.compile(
            "\\{[^{}]*\"agent\"[^{}]*\"task\"[^{}]*}",
            Pattern.DOTALL
    );

    /**
     * 解析思考输出
     */
    private ReActState.AgentAction parseThinkOutput(String output, ReActState state) {
        // 提取JSON部分
        Matcher matcher = JSON_PATTERN.matcher(output);
        if (matcher.find()) {
            try {
                String json = matcher.group();
                AgentRouteDecision decision = objectMapper.readValue(json, AgentRouteDecision.class);
                return ReActState.AgentAction.builder()
                        .agent(decision.getAgent())
                        .task(decision.getTask())
                        .reason(decision.getReason())
                        .build();
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse action JSON", e);
            }
        }

        // 降级处理
        String lowerOutput = output.toLowerCase();
        if (lowerOutput.contains("rag")) {
            return ReActState.AgentAction.builder()
                    .agent("rag-agent")
                    .task(state.getOriginalRequest().getMessage())
                    .reason("关键词匹配: rag")
                    .build();
        } else if (lowerOutput.contains("publish")) {
            return ReActState.AgentAction.builder()
                    .agent("publish-agent")
                    .task(state.getOriginalRequest().getMessage())
                    .reason("关键词匹配: publish")
                    .build();
        }

        return ReActState.AgentAction.builder()
                .agent("assistant-agent")
                .task(state.getOriginalRequest().getMessage())
                .reason("无法确定具体代理，使用默认代理")
                .build();
    }

    /**
     * 提取Thought内容
     */
    private String extractThought(String output) {
        Pattern pattern = Pattern.compile("Thought:\\s*(.+?)(?=(?:Action:|Final Answer:|$))", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(output);
        if (matcher.find()) {
            log.info("extractThought: {}", matcher.group(1).trim());
            return matcher.group(1).trim();
        }
        return output;
    }

    /**
     * 提取最终答案
     */
    private String extractFinalAnswer(String output) {
        Pattern pattern = Pattern.compile("Final Answer:\\s*(.+)$", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(output);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return output;
    }
}

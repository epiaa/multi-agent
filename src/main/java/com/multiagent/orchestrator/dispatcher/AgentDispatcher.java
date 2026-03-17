package com.multiagent.orchestrator.dispatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multiagent.agent.Agent;
import com.multiagent.agent.AgentFactory;
import com.multiagent.agent.AgentRequest;
import com.multiagent.agent.dto.AgentRouteDecision;
import com.multiagent.agent.registry.AgentRegistry;
import com.multiagent.orchestrator.chatagent.ChatAgentService;
import com.multiagent.orchestrator.config.SupervisorAgentService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Agent调度器
 * 负责解析Supervisor决策并分发任务到具体Agent
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AgentDispatcher {

    private final AgentFactory agentFactory;
    private final ObjectMapper objectMapper;

    /**
     * JSON提取正则（处理LLM可能在JSON前后添加额外文本的情况）
     */
    private static final Pattern JSON_PATTERN = Pattern.compile("\\{[^{}]*\"agent\"[^{}]*\"task\"[^{}]*}", Pattern.DOTALL);

    public String dispatch(AgentRequest request) {

        Agent assistant = agentFactory.getAgent("assistant-agent");

        SystemMessage systemMessage = new SystemMessage(
                """
                        你是一个智能任务调度器，负责分析用户意图并将任务分发给合适的专业Agent处理。

                        ## 可用的专业Agent：
                        ### 1. rag-agent（知识问答Agent）
                        - 职责：回答课程相关的知识问题，提供技术讲解和知识检索
                        - 适用场景：
                          * 用户询问技术概念、编程知识
                          * 需要查询课程内容、学习资料
                          * 技术问答、知识解释
                        - 示例问题："什么是Java线程池？"、"解释Spring的IoC原理"

                        ### 2. publish-agent（课程订阅Agent）
                        - 职责：管理用户的课程订阅，执行数据库操作
                        - 适用场景：
                          * 订阅/取消订阅课程
                          * 查看已订阅课程列表
                          * 查看所有可用课程
                        - 示例问题："帮我订阅Java课程"、"我订阅了哪些课程？"

                        ### 3. assistant-agent
                        - 职责：处理简单对话、问候、以及不属于上述Agent的问题
                        - 适用场景：
                          * 简单问候、闲聊
                          * 功能介绍、帮助说明
                          * 无法归类到其他Agent的问题

                        ## 输出格式要求：

                        你必须严格返回以下JSON格式（不要包含任何其他文本）：
                        ```json
                        {
                            "agent": "agent名称",
                            "task": "要执行的具体任务描述",
                            "reason": "选择该Agent的理由"
                        }
                        ```

                        agent字段只能是以下值之一：
                        - "rag-agent"
                        - "publish-agent"  
                        - "assistant-agent"

                        ## 示例：

                        用户: "帮我订阅Python入门课程"
                        你的返回:
                        {"agent": "publish-agent", "task": "帮用户订阅Python入门课程", "reason": "涉及课程订阅操作"}

                        用户: "什么是微服务架构？"
                        你的返回:
                        {"agent": "rag-agent", "task": "解释微服务架构的概念", "reason": "属于技术知识问答"}

                        用户: "你好"
                        你的返回:
                        {"agent": "assistant-agent", "task": "友好地回复用户问候", "reason": "简单问候，无需专业Agent"}

                        """
        );
        UserMessage userMessage = new UserMessage(request.getMessage());
        List<ChatMessage> cml = new ArrayList<>();
        cml.add(systemMessage);
        cml.add(userMessage);
        request.setMessage(cml.toString());
        String decisionJson = String.valueOf(assistant.execute(request).getOutput());
        log.info("决策: {}", decisionJson);

        // 2. 解析决策
        AgentRouteDecision decision = parseDecision(decisionJson);

        // 3. 路由到指定Agent
        String agentName = decision.getAgent();
        Agent agent = agentFactory.getAgent(agentName);

        if (agent == null) {
            log.warn("未找到Agent: {}, 由Assistant处理", agentName);
            return decisionJson;
        }

        // 4. 执行任务
        String task = decision.getTask() != null ? decision.getTask() : request.getMessage();
        log.info("路由到Agent: {}, 任务: {}", agentName, task);

        return task;
    }

    /**
     * 解析LLM返回的决策JSON
     * 支持从混合文本中提取JSON
     */
    private AgentRouteDecision parseDecision(String response) {
        try {
            // 尝试直接解析
            return objectMapper.readValue(response, AgentRouteDecision.class);
        } catch (JsonProcessingException e) {
            // 从文本中提取JSON
            Matcher matcher = JSON_PATTERN.matcher(response);
            if (matcher.find()) {
                try {
                    return objectMapper.readValue(matcher.group(), AgentRouteDecision.class);
                } catch (JsonProcessingException ex) {
                    log.warn("JSON解析失败: {}", matcher.group());
                }
            }
        }

        // 降级处理：尝试从文本中识别关键词
        return fallbackParse(response);
    }

    /**
     * 降级解析：关键词匹配
     */
    private AgentRouteDecision fallbackParse(String response) {
        String lowerResponse = response.toLowerCase();
        
        if (lowerResponse.contains("rag")) {
            return new AgentRouteDecision("rag-agent", null, "关键词匹配: rag");
        } else if (lowerResponse.contains("publish")) {
            return new AgentRouteDecision("publish-agent", null, "关键词匹配: publish");
        }
        // 默认由Assistant处理
        return new AgentRouteDecision("assistant-agent", null, "无法解析，默认处理");
    }
}

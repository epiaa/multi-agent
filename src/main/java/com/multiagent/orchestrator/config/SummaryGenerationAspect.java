package com.multiagent.orchestrator.config;

import dev.langchain4j.data.message.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SummaryGenerationAspect {

    private final SummaryService summaryService;
    private final ThreadPoolTaskExecutor taskExecutor;


    @AfterReturning(
            pointcut = """
                execution(* com.multiagent.orchestrator.config.RedisChatMessageStore.updateMessages(..))
                &&  args(o, messages)
                """)
    public void afterUpdateMessages(JoinPoint joinPoint, Object o, List<ChatMessage> messages){
        if (shouldGenerateSummary(o, messages)) {
            // 异步生成摘要
            taskExecutor.execute(() -> {
                try {
                    summaryService.generateAndSaveSummary(o, messages);
                } catch (Exception e) {
                    log.error("AOP触发生成摘要失败: {}", o, e);
                }
            });
        }
    }

    private boolean shouldGenerateSummary(Object o, List<ChatMessage> messages) {
        // 摘要生成判断逻辑
        return messages.size() >= 8;
    }

}

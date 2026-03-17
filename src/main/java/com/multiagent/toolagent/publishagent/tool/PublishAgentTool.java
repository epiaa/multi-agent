package com.multiagent.toolagent.publishagent.tool;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.multiagent.common.entity.UserHolder;
import com.multiagent.toolagent.publishagent.business.service.CourseService;
import com.multiagent.toolagent.publishagent.business.service.PublishService;
import com.multiagent.toolagent.publishagent.entity.CoursePO;
import com.multiagent.toolagent.publishagent.entity.PublishPO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PublishAgentTool {

    private final PublishService publishService;
    private final CourseService courseService;

    @Tool("获取用户订阅的所有课程")
    public List<CoursePO> getAllPublish() {
        Long userId = UserHolder.getUser().getId();

        // 1. 获取用户订阅的课程ID列表
        List<Long> courseIds = publishService.lambdaQuery()
                .eq(PublishPO::getUserId, userId)
                .isNull(PublishPO::getDeleteTime)
                .list()
                .stream()
                .map(PublishPO::getCourseId)
                .collect(Collectors.toList());  // 收集为List<Long>

        // 2. 如果用户没有订阅任何课程，直接返回空列表
        if (CollectionUtils.isEmpty(courseIds)) {
            return Collections.emptyList();
        }

        // 3. 根据课程ID列表查询课程信息
        return courseService.lambdaQuery()
                .in(CoursePO::getId, courseIds)  // 使用List<Long>
                .list();
    }

    @Tool("获取全部课程")
    public List<CoursePO> getAllCourse() {
        return courseService.list();
    }

    @Tool("根据课程Id：courseId订阅课程")
    public boolean addPublish(
            @P("课程Id")Long courseId
    ){
        Long userId = UserHolder.getUser().getId();
        try{
            PublishPO publishPO = PublishPO.builder()
                    .userId(userId)
                    .courseId(courseId)
                    .publishTime(LocalDateTime.now())
                    .build();
            publishService.save(publishPO);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Tool("根据课程Id订阅课程")
    public boolean delPublish(
            @P("课程Id")Long courseId
    ){
        Long userId = UserHolder.getUser().getId();
        return publishService.lambdaUpdate()
                    .eq(PublishPO::getUserId, userId)
                    .eq(PublishPO::getCourseId, courseId)
                    .set(PublishPO::getDeleteTime, LocalDateTime.now())
                    .update();
    }
}

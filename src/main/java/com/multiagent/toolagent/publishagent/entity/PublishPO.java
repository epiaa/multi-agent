package com.multiagent.toolagent.publishagent.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("tb_publish")
public class PublishPO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long courseId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime publishTime;

    private LocalDateTime deleteTime;
}

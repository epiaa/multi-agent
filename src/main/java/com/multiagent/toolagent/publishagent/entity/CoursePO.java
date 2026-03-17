package com.multiagent.toolagent.publishagent.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("tb_course")
public class CoursePO{
    @TableId(type = IdType.AUTO)
    private Long id;

    /*
     * 课程名
     * */
    private String title;

    /*
     * 课程介绍
     * */
    private String content;

    /*
     * 授课老师
     * */
    private String teacher;
}

package com.multiagent.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_user")
public class UserPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private Integer balance;
}

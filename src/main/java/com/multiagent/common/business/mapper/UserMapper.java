package com.multiagent.common.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.multiagent.common.entity.UserPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
}

package com.multiagent.common.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.multiagent.common.business.mapper.UserMapper;
import com.multiagent.common.entity.UserLogin;
import com.multiagent.common.entity.UserPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends ServiceImpl<UserMapper, UserPO> {
    private final StringRedisTemplate redisTemplate;

    public String login(UserLogin userLogin) {
        String phone = userLogin.getPhone();
        String code = userLogin.getCode();
        String verifyCode = redisTemplate.opsForValue().get("LOGIN:CODE:" + phone);
        if (code == null || !code.equals(verifyCode)) {
            return "验证码错误";
        }
        UserPO user = lambdaQuery().eq(UserPO::getPhone, phone).one();
        if (user == null) {
            user = new UserPO();
            user.setPhone(phone);
            user.setBalance(0);
            save(user);
        }

        Map<String, Object> map = BeanUtil.beanToMap(user, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((name, value) -> value.toString()));
        String token = UUID.randomUUID().toString().replace("-", "");
        String key = "LOGIN:USER:" + token;
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, 60 * 60 * 5, TimeUnit.SECONDS);
        return token;
    }

    public void send(String phone) {
        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().set("LOGIN:CODE:" + phone, JSONUtil.toJsonStr(code), 60, TimeUnit.SECONDS);
        log.info("验证码已发送至手机: {}, 验证码: {}", phone, code);
    }
}

package com.multiagent.common.interceptor;

import cn.hutool.core.bean.BeanUtil;

import com.multiagent.common.entity.UserHolder;
import com.multiagent.common.entity.UserPO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;


public class LoginInterceptor implements HandlerInterceptor {

    private StringRedisTemplate redisTemplate;

    public LoginInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()){
            response.setStatus(401);
            return false;
        }
        String key = "LOGIN:USER:" + token;
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        if (map.isEmpty()) {
            response.setStatus(401);
            return false;
        }
        UserPO user = BeanUtil.fillBeanWithMap(map, new UserPO(), false);
        UserHolder.setUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}

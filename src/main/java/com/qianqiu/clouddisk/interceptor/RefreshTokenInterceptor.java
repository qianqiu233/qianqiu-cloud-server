package com.qianqiu.clouddisk.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.qianqiu.clouddisk.model.dto.UserInfoAndToken;
import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import com.qianqiu.clouddisk.utils.UserThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;


import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.qianqiu.clouddisk.utils.Constant.RedisConstant.USER_INFO_KEY;
import static com.qianqiu.clouddisk.utils.Constant.RedisConstant.USER_INFO_KEY_TTL;

@Slf4j
public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求头中的token
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return true;
        }
        // 2.基于TOKEN获取redis中的用户
        String key = USER_INFO_KEY + token;
        String userStr = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isEmpty(userStr)){
            return true;
        }
        UserInfoVo userInfoVo = JSONUtil.toBean(userStr, UserInfoVo.class);
        log.info("用户信息:{}",userInfoVo);
        // 6.存在，保存用户信息到 ThreadLocal
        UserInfoAndToken userInfoAndToken = new UserInfoAndToken(userInfoVo,token);
        UserThreadLocal.saveUserInfoAndToken(userInfoAndToken);
        // 7.刷新token有效期
        stringRedisTemplate.expire(key, USER_INFO_KEY_TTL, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        UserThreadLocal.removeUser();
    }
}

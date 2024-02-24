package com.qianqiu.clouddisk.tasks;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.qianqiu.clouddisk.model.dto.SysSettingDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.qianqiu.clouddisk.utils.Constant.RedisConstant.SYSTEM_KEY;

@Component
@Slf4j
public class ScheduledTasks {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @PostConstruct
    public void init() {
        if (stringRedisTemplate != null) {
            LoadConfiguration();
        } else {
            log.error("StringRedisTemplate 未成功注入，无法执行初始化任务");
        }
    }
    public void LoadConfiguration() {
        //加载配置
        String redisSystem = stringRedisTemplate.opsForValue().get(SYSTEM_KEY);
        if (StrUtil.isBlank(redisSystem)){
            log.info("缓存中无配置数据，现在进行初始化配置");
            SysSettingDTO sysSettingDTO = new SysSettingDTO();
            stringRedisTemplate.opsForValue().set(SYSTEM_KEY, JSONUtil.toJsonStr(sysSettingDTO));
            log.info("重新初始化配置成功");
        }else {
            log.info("缓存中存在配置，无需初始化");
        }

    }
}

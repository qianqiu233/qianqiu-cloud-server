package com.qianqiu.clouddisk.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.qianqiu.clouddisk.mbg.mbg_mapper")
public class MybatisConfig {
}

package com.qianqiu.clouddisk.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc API文档相关配置
 */
@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI mallTinyOpenAPI(@Value("${version}") String appVersion) {
        return new OpenAPI()
                .info(new Info().title("千秋云盘")
                        .description("千秋云盘接口文档")
                        .version(appVersion)
                        .contact(new Contact()
                                .name("qianqiu")
                                .url("https://github.com/qianqiu233/qianqiu-cloud-server.git")
                                .email("2799611325@qq.com"))
                        .license(new License().name("qianqiu-cloud-github").url("https://github.com/qianqiu233/qianqiu-cloud-server.git")));
    }
}
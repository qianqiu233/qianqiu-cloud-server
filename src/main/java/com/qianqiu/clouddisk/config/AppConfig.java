package com.qianqiu.clouddisk.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AppConfig {
    @Value("${save.fileRootPackage}")
    private String fileRootPackage;
}

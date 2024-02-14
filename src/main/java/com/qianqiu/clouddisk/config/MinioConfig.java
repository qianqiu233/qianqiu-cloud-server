package com.qianqiu.clouddisk.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Data
@Configuration
public class MinioConfig {
    @Value("${minio.endpoint}")
    private String ENDPOINT;
    @Value("${minio.accessKey}")
    private String ACCESS_KEY;
    @Value("${minio.secretKey}")
    private String SECRET_KEY;

    @Bean
    public MinioClient minioClient() {
        Assert.hasText(ACCESS_KEY, "Minio accessKey为空");
        Assert.hasText(SECRET_KEY, "Minio secretKey为空");
        return MinioClient.builder()
                .endpoint(ENDPOINT)
                .credentials(ACCESS_KEY, SECRET_KEY)
                .build();
    }
}

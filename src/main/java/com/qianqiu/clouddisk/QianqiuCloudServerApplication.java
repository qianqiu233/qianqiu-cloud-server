package com.qianqiu.clouddisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class QianqiuCloudServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(QianqiuCloudServerApplication.class, args);
    }

}

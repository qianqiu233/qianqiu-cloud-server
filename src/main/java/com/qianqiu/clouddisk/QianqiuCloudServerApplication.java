package com.qianqiu.clouddisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
public class QianqiuCloudServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(QianqiuCloudServerApplication.class, args);
        System.out.println("----启动成功---");
    }

}

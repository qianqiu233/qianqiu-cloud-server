package com.qianqiu.clouddisk.annotation;

import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableCheck {
    /**
     * 是否开启校验
     * @return
     */
    boolean checkParams() default true;
}

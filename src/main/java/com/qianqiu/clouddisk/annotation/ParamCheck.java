package com.qianqiu.clouddisk.annotation;

import com.qianqiu.clouddisk.utils.Regexs.RegexPatterns;
import com.qianqiu.clouddisk.utils.enums.RegexEnum;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Documented
public @interface ParamCheck {
    /**
     * 校验正则
     * @return
     */
    RegexEnum regex() default RegexEnum.NO;

    /**
     * 是否是必须参数
     * @return
     */
    boolean required() default true;


}

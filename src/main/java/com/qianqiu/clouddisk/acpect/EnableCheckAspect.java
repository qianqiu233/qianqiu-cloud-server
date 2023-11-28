package com.qianqiu.clouddisk.acpect;

import cn.hutool.core.util.StrUtil;
import com.qianqiu.clouddisk.annotation.EnableCheck;
import com.qianqiu.clouddisk.annotation.ParamCheck;
import com.qianqiu.clouddisk.exception.CommonException;
import com.qianqiu.clouddisk.utils.Regexs.RegexUtils;
import com.qianqiu.clouddisk.utils.enums.RegexEnum;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.qianqiu.clouddisk.utils.commonResult.ResultCode.VALIDATE_FAILED;


@Component
@Aspect
public class EnableCheckAspect {
    private static Logger logger = LoggerFactory.getLogger(EnableCheckAspect.class);
    private static final List<String> ARGS_TYPE = Collections
            .unmodifiableList(Arrays.asList(
                    "java.lang.String",
                    "java.lang.Integer",
                    "java.lang.Long"));

    @Pointcut("@annotation(com.qianqiu.clouddisk.annotation.EnableCheck)")
    private void EnableCheck() {
    }

    @Before("EnableCheck()")
    public void CheckStart(JoinPoint point) throws NoSuchMethodException {
        Object target = point.getTarget();
        Object[] targetArgs = point.getArgs();
        MethodSignature signature = (MethodSignature) point.getSignature();
        //获取方法名字
        String methodName = signature.getName();
        //获取参数类型数组
        Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
        //定位到方法
        Method method = target.getClass().getMethod(methodName, parameterTypes);
        //查看该方法上是否有@EnableCheck注解
        EnableCheck enableCheck = method.getAnnotation(EnableCheck.class);
        if (enableCheck == null) {
            //没有注解，直接跳过
            return;
        }
        /**
         * 校验参数
         */
        if (enableCheck.checkParams()) {
            enableCheckParams(method, targetArgs);
        }

    }

    /**
     * 校验参数
     *
     * @param method     方法对象
     * @param targetArgs 方法的参数内容
     */
    private void enableCheckParams(Method method, Object[] targetArgs) {
        //获取method的参数类型数组
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            //拿到内容
            Object ArgsValue = targetArgs[i];
            if (ArgsValue==null){
                continue;
            }
            //判断参数上是否有ParamCheck注解
            ParamCheck paramCheck = parameter.getAnnotation(ParamCheck.class);
            if (paramCheck == null) {
                //下一个
                continue;
            }
            //判断是什么类型，正常包装类型或自定义类型
            String typeName = parameter.getParameterizedType().getTypeName();
            if (ARGS_TYPE.contains(typeName)) {
                //正常包装类
                checkValue(ArgsValue, paramCheck);
            } else {
                //自定义类型
                checkObjValue(ArgsValue, parameter);
            }
        }
    }

    /**
     * 校验自定义类型
     *
     * @param argsValue
     * @param parameter
     */
    private void checkObjValue(Object argsValue, Parameter parameter) {
        String typeName = parameter.getParameterizedType().getTypeName();
        try {
            //获取类
            Class<?> aClass = Class.forName(typeName);
            //获取所有属性
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                ParamCheck paramCheck = field.getAnnotation(ParamCheck.class);
                if (paramCheck == null) {
                    continue;
                }
                field.setAccessible(true);
                Object resValue = field.get(argsValue);
                checkValue(resValue, paramCheck);
            }
        } catch (ClassNotFoundException | IllegalAccessException e) {
            throw new CommonException("参数校验失败");
        }
    }

    /**
     * 校验正常包装类型参数
     *
     * @param argsValue
     * @param paramCheck
     */
    private void checkValue(Object argsValue, ParamCheck paramCheck) {
        //如果传入的字符串是 null、空字符串 ""，或者只包含空白字符，返回 true；
        boolean isEmpty = StrUtil.isBlank(argsValue.toString());
        //空的
        if (isEmpty) {
            if (paramCheck.required()) {
                throw new CommonException(VALIDATE_FAILED.getCode(),"该参数不能为空");
            }
        }
        //不是空的
        String regex = paramCheck.regex().getRegex();
        if (!RegexEnum.NO.getRegex().equals(regex)){
            if (!RegexUtils.matchSuccess(argsValue.toString(), regex)) {
                throw new CommonException(VALIDATE_FAILED.getCode(),"参数格式错误");
            }
        }
    }
}

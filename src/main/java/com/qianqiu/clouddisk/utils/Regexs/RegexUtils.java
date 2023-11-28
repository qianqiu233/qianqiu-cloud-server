package com.qianqiu.clouddisk.utils.Regexs;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RegexUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegexUtils.class);
    public static boolean isPhoneInvalid(String phone){
        return matchSuccess(phone, RegexPatterns.PHONE_REGEX);
    }
    public static boolean isEmailInvalid(String email){
        return matchSuccess(email, RegexPatterns.EMAIL_REGEX);
    }
    
    public static boolean isCodeInvalid(String code){
        return matchSuccess(code, RegexPatterns.VERIFY_CODE_REGEX);
    }
    
    public static boolean isPasswordInvalid(String code){
        return matchSuccess(code, RegexPatterns.PASSWORD_REGEX);
    }
    // 校验是否不符合正则格式
    public static boolean matchSuccess(String str, String regex){
        if (StrUtil.isBlank(str)) {
            return false;
        }
        boolean res =str.matches(regex);
        return res;
    }
}

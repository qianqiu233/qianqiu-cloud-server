package com.qianqiu.clouddisk.utils;

import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import java.awt.*;

@Slf4j
public class CaptchaUtil {
    public Captcha generateCaptcha(HttpServletResponse response, String type) {
        response.setContentType("image/gif");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        Captcha captcha = null;
        int len=6;
        switch (type) {
            case "png":
                captcha = new SpecCaptcha(130, 48,len);
                break;
            case "gif":
                // gif类型
                captcha = new GifCaptcha(130, 48,len);
                break;
            case "cn":
                // 中文类型
                captcha = new ChineseCaptcha(130, 48, len, new Font("楷体", Font.PLAIN, 28));
                break;
            case "cngif":
                // 中文gif类型
                captcha = new ChineseGifCaptcha(130, 48, len, new Font("楷体", Font.PLAIN, 28));
                break;
            case "arithmeti":
                // 算术类型
                ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(130, 48);
                arithmeticCaptcha.setLen(3);  // 几位数运算，默认是两位
                arithmeticCaptcha.getArithmeticString();  // 获取运算的公式：3+2=?
                arithmeticCaptcha.text();  // 获取运算的结果：5
                captcha = arithmeticCaptcha;
                break;
            default:
                new SpecCaptcha(130, 48);
                break;
        }
        // 设置字体
        //captcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        assert captcha != null;
        captcha.setCharType(Captcha.TYPE_NUM_AND_UPPER);
        return captcha;
    }
}

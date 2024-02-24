package com.qianqiu.clouddisk.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.DEFAULT_USER_TOTAL_SPACE;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysSettingDTO {
    /**
     * 注册发送邮件标题
     */
    private String registerEmailTitle = "邮箱验证码";

    /**
     * 注册发送邮件内容
     */
    private String registerEmailContent = "你好，您的邮箱验证码是：%s，15分钟有效";

    /**
     * 用户初始化空间大小 5G，按mbsauna，参的时候需要*mb
     */
    private Long userInitTotalSpace = DEFAULT_USER_TOTAL_SPACE;

}

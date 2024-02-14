package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.annotation.ParamCheck;
import com.qianqiu.clouddisk.utils.enums.RegexEnum;
import lombok.Data;

@Data
public class LoginDTO {
    @ParamCheck(regex = RegexEnum.EMAIL)
    private String email;
    @ParamCheck
    private String password;
    @ParamCheck
    private String checkCode;
    @ParamCheck
    private String key;
}

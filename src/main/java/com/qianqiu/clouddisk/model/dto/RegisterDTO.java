package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.annotation.ParamCheck;
import com.qianqiu.clouddisk.utils.enums.RegexEnum;
import lombok.Data;

@Data
public class RegisterDTO {
    @ParamCheck(regex = RegexEnum.EMAIL)
    private String email;
    @ParamCheck
    private String nickName;
    @ParamCheck
    private String password;
    @ParamCheck
    private String checkCode;
    @ParamCheck
    private String emailCode;
    @ParamCheck
    private String key;
}

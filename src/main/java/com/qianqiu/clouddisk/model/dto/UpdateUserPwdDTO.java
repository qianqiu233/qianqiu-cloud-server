package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.annotation.ParamCheck;
import com.qianqiu.clouddisk.utils.enums.RegexEnum;
import lombok.Data;

@Data
public class UpdateUserPwdDTO {
    @ParamCheck(regex = RegexEnum.PASSWORD)
    private String oldPassword;
    @ParamCheck(regex = RegexEnum.PASSWORD)
    private String newPassword;

}

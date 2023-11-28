package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.annotation.ParamCheck;
import lombok.Getter;
import lombok.Setter;

public class ReSetPwdDTO extends LoginDTO{
    @Getter
    @Setter
    @ParamCheck
    private String emailCode;
}

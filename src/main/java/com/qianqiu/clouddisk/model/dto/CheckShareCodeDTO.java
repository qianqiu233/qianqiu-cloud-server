package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.annotation.ParamCheck;
import lombok.Data;

@Data
public class CheckShareCodeDTO {
    @ParamCheck
    private String shareId;
    @ParamCheck
    private String code;
}

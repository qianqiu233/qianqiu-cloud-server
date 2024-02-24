package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.annotation.ParamCheck;
import lombok.Data;

@Data
public class UpdateUserStatusDTO {
    @ParamCheck
    private String userId;
    @ParamCheck
    private Integer status;
}

package com.qianqiu.clouddisk.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ShareInfo extends FileShareDTO{
    private String nickName;
    private String avatar;
}

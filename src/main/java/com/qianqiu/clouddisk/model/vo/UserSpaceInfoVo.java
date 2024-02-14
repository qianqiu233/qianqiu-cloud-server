package com.qianqiu.clouddisk.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSpaceInfoVo {
    private Long useSpace;
    private Long totalSpace;
}

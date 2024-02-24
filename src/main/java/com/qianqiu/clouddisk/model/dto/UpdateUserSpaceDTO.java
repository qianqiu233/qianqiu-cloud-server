package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.annotation.ParamCheck;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserSpaceDTO {
    @ParamCheck
    private String userId;
    //修改空间大小，默认是mb 1024*1024
    @ParamCheck
    private Long changeSpace;
    @ParamCheck
    private Long totalSpace;
    @ParamCheck
    private Integer flag;

}

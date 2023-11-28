package com.qianqiu.clouddisk.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserInfoVo {
    private String userId;
    private String nickName;
    private Object avatar;
    private Boolean admin;
    // todo 想改成资源
//    private List<String> userResources;
}

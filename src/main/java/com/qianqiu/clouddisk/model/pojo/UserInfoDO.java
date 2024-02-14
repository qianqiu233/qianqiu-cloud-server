package com.qianqiu.clouddisk.model.pojo;

import lombok.Data;

@Data
public class UserInfoDO {
    private String userId;
    private String nickName;
    private Object avatar;
    private Boolean admin;
    // todo 想改成资源
//    private List<String> userResources;
}

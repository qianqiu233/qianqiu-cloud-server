package com.qianqiu.clouddisk.model.vo;

import lombok.Data;
@Data
public class UserLoginVo {
//todo 暂用，之后肯定删除
    private UserInfoVo userInfoVo;
    private String token;

    public UserLoginVo(UserInfoVo userInfoVo, String token) {
        this.userInfoVo = userInfoVo;
        this.token = token;
    }
}

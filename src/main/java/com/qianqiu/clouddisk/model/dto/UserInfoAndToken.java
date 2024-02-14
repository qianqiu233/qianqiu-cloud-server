package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.model.vo.UserInfoVo;

public class UserInfoAndToken {
    private UserInfoVo userInfo;
    private String token;

    public UserInfoAndToken(UserInfoVo userInfo, String token) {
        this.userInfo = userInfo;
        this.token = token;
    }

    public UserInfoVo getUserInfo() {
        return userInfo;
    }

    public String getToken() {
        return token;
    }

    public void setUserInfo(UserInfoVo userInfo) {
        this.userInfo = userInfo;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

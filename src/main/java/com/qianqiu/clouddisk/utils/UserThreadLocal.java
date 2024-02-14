package com.qianqiu.clouddisk.utils;


import com.qianqiu.clouddisk.exception.CommonException;
import com.qianqiu.clouddisk.model.dto.UserInfoAndToken;
import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import com.qianqiu.clouddisk.utils.commonResult.ResultCode;

public class UserThreadLocal {
    private static final ThreadLocal<UserInfoAndToken> tl = new ThreadLocal<>();

    public static void saveUserInfoAndToken(UserInfoAndToken userInfoAndToken){
        tl.set(userInfoAndToken);
    }

    public static UserInfoVo getUser(){
        try {
            UserInfoAndToken userInfoAndToken = tl.get();
            UserInfoVo userInfo = userInfoAndToken.getUserInfo();
            return userInfo;
        }catch (Exception e){
            throw new CommonException(ResultCode.UNAUTHORIZED.getCode(),ResultCode.UNAUTHORIZED.getMsg());
        }

    }
    public static String getUserId(){
        try {
            UserInfoVo user = getUser();
            return user.getUserId();
        }catch (Exception e){
            throw new CommonException(ResultCode.UNAUTHORIZED);
        }
    }
    public static String getToken(){
        try {
            UserInfoAndToken userInfoAndToken = tl.get();
            String token = userInfoAndToken.getToken();
            return token;
        }catch (Exception e){
            throw new CommonException(ResultCode.UNAUTHORIZED);
        }

    }
    public static void removeUser(){
        tl.remove();
    }
}

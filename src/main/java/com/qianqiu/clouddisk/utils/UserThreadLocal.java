package com.qianqiu.clouddisk.utils;


import com.qianqiu.clouddisk.model.vo.UserInfoVo;

public class UserThreadLocal {
    private static final ThreadLocal<UserInfoVo> tl = new ThreadLocal<>();

    public static void saveUser(UserInfoVo user){
        tl.set(user);
    }

    public static UserInfoVo getUser(){
        return tl.get();
    }
    public static String getUserId(){
        return tl.get().getUserId();

    }
    public static void removeUser(){
        tl.remove();
    }
}

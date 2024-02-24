package com.qianqiu.clouddisk.utils.Constant;

public class RedisConstant {
    public static final String LOGIN_CODE_KEY="user:login:code:";
    public static final Long LOGIN_CODE_KEY_TTL=5L;
    public static final String LOGIN_CODE_EMAIL_KEY="user:login:emailCode:";
    public static final Long LOGIN_CODE_EMAIL_KEY_TTL=5L;
    public static final String USER_INFO_KEY="user:info:";
    public static final Long USER_INFO_KEY_TTL=30L;
    public static final String USER_SPACE_KEY="user:userSpace:";
    public static final Long USER_SPACE_KEY_TTL=10L;
    public static final String USER_DOWNLOAD_KEY ="user:download:";
    public static final String ADMIN_DOWNLOAD_KEY ="admin:download:";
    public static final String WEB_SHARE_DOWNLOAD_KEY ="webShare:download:";
    public static final Long DOWNLOAD_KEY_TTL=30L;
    public static final String PREVIEW_KEY="preview:";
    public static final Long PREVIEW_KEY_TTL=30L;
    public static final String SYSTEM_KEY="system:";

}

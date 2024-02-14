package com.qianqiu.clouddisk.utils.enums;

/**
 * 文件使用标记枚举
 */
public enum  FileUseFlagEnums {
    DEL(0, "删除"),
    RECYCLE (1, "回收站"),
    USING(2, "使用中"),
    USEINGAVATAR(3,"使用中的头像"),
    UNUSEINGAVATAR(4,"历史头像");


    private final Integer flag;
    private final String desc;

    FileUseFlagEnums(Integer flag, String desc) {
        this.flag = flag;
        this.desc = desc;
    }

    public Integer getFlag() {
        return flag;
    }

    public String getDesc() {
        return desc;
    }
}

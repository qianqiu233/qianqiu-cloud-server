package com.qianqiu.clouddisk.utils.enums;

public enum FileStatusEnum {
    TRANSCODING(0, "转码中"),
    TRANSCODING_FAIL(1, "转码失败"),
    TRANSCODING_SUCCESS(2, "转码成功");
    private final Integer code;
    private final String desc;

    FileStatusEnum(Integer flag, String desc) {
        this.code = flag;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

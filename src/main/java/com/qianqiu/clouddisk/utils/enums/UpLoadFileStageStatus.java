package com.qianqiu.clouddisk.utils.enums;

public enum UpLoadFileStageStatus {
    FAIL(-1,"初始化阶段失败"),
    NORMAL(0,"普通文件上传阶段"),
    SLICE(1,"切片文件上传阶段"),
    CONTINUED(2,"续传文件阶段"),
    UPLOADED(3,"已上传文件"),
    MERGE(4,"合并文件阶段"),
    UPLOAD_SUCCESS(5,"文件成功上传");
    private final Integer upLoadStatus;
    private final String  desc;

    public Integer getUpLoadStatus() {
        return upLoadStatus;
    }

    public String getDesc() {
        return desc;
    }

    UpLoadFileStageStatus(Integer upLoadStatus, String desc) {
        this.upLoadStatus = upLoadStatus;
        this.desc = desc;
    }
}

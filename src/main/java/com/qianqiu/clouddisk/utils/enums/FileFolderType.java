package com.qianqiu.clouddisk.utils.enums;

public enum FileFolderType {
    FILE(0,"文件"),
    DIRECTORY(1,"目录");
    private final Integer folderCode;
    private final String  folderName;

    FileFolderType(Integer folderCode, String folderName) {
        this.folderCode = folderCode;
        this.folderName = folderName;
    }

    public Integer getFolderCode() {
        return folderCode;
    }

    public String getFolderName() {
        return folderName;
    }
}

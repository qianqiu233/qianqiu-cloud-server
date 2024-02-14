package com.qianqiu.clouddisk.utils.enums;

public enum FileTypeEnums {
    FOLDER(0, "folder"),
    VIDEO(1, "video"),
    AUDIO(2, "audio"),
    IMAGE(3, "image"),
    PDF(4,"pdf"),
    DOC(5,"doc"),
    EXCEL(6,"excel"),
    TXT(7,"txt"),
    CODE(8,"code"),
    ZIP(9,"zip"),
    OTHER(10,"other");
    private final Integer typeCode;
    private final String  typeName;

    FileTypeEnums(Integer typeCode, String typeName) {
        this.typeCode = typeCode;
        this.typeName = typeName;
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public String getTypeName() {
        return typeName;
    }
}

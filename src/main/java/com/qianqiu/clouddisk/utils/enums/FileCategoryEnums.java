package com.qianqiu.clouddisk.utils.enums;

/**
 * 文件类型枚举类
 */
public enum FileCategoryEnums {
    FOLDER(0,"folder","/folder","文件夹"),
    VIDEO(1, "video", "/video","视频"),
    AUDIO(2, "audio", "/audio","音频"),
    IMAGE(3, "image", "/image","图片"),
    DOC(4, "doc", "/doc","文档"),
    OTHERS(5, "others", "/others","其他");

    private final Integer category;
    private final String code;
    private final String packageName;
    private final String desc;

    FileCategoryEnums(Integer category, String code, String packageName,String desc) {
        this.category = category;
        this.code = code;
        this.packageName=packageName;
        this.desc = desc;
    }

    /**
     * 根据前端传过来的categoryCode，查找对应的CategoryEnum
     * @param code
     * @return
     */
    public static FileCategoryEnums getByCode(String code){
        FileCategoryEnums[] values = FileCategoryEnums.values();
        for (FileCategoryEnums value : values) {
            if (value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }
    public Integer getCategory() {
        return category;
    }

    public String getCode() {
        return code;
    }

    public String getPackageName() {
        return packageName;
    }
}

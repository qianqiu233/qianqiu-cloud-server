package com.qianqiu.clouddisk.model.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public class MinioFileInfo {
    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private String fileId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String userId;

    /**
     * md5值，第一次上传记录
     */
    @Schema(description = "md5值，第一次上传记录")
    private String fileMd5;

    /**
     * 父级ID
     */
    @Schema(description = "父级ID")
    private String filePid;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 文件名称
     */
    @Schema(description = "文件名称")
    private String fileName;

    /**
     * 封面
     */
    @Schema(description = "封面")
    private String fileCover;

    /**
     * 文件路径
     */
    @Schema(description = "文件路径")
    private String filePath;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 最后更新时间
     */
    @Schema(description = "最后更新时间")
    private Date lastUpdateTime;

    /**
     * 0:文件 1:目录
     */
    @Schema(description = "0:文件 1:目录")
    private Integer folderType;

    /**
     * 1:视频 2:音频  3:图片 4:文档 5:其他
     */
    @Schema(description = "1:视频 2:音频  3:图片 4:文档 5:其他")
    private Integer fileCategory;
}

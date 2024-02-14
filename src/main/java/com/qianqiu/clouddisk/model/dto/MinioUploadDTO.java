package com.qianqiu.clouddisk.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@Builder
@EqualsAndHashCode
public class MinioUploadDTO {
    @Schema(description = "文件访问URL")
    private String fileUrl;
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
     * 文件路径
     */
    @Schema(description = "文件包名路径")
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
     * 1:视频 2:音频  3:图片 4:文档 5:其他
     */
    @Schema(description = "1:视频 2:音频  3:图片 4:文档 5:其他")
    private Integer fileCategory;
    @Schema(description = "文件类型")
    private String fileContentType;
    @Schema(description = "-1:上传完成 -2:合并完成 -3 无需合并 -4 正在上传")
    private Integer uploadFlag;
}

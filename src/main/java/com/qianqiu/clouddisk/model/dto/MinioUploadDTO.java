package com.qianqiu.clouddisk.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MinioUploadDTO {
    @Schema(description = "文件访问URL")
    private String FileUrl;
    @Schema(description = "文件名称")
    private String FileName;
}

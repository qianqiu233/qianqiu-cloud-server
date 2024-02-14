package com.qianqiu.clouddisk.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpLoadSliceFileDTO {
    @Schema(description = "文件id")
    private String fileId;
    @Schema(description = "文件上传状态")
    private String status;
    @Schema(description = "文件初始化状态")
    private Integer UpLoadFileStatus;
}

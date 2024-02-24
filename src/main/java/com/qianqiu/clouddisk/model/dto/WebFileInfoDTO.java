package com.qianqiu.clouddisk.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class WebFileInfoDTO {
    private String fileId;
    private String filePid;
    private Long fileSize;
    private String fileName;
    private Date lastUpdateTime;
    private Integer folderType;
    private Integer fileCategory;
    private Integer fileType;
    private Integer status;
    private Integer useFlag;
    private String fileCover;
}

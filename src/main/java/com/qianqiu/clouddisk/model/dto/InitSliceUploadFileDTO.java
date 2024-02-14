package com.qianqiu.clouddisk.model.dto;

import lombok.Data;

@Data
public class InitSliceUploadFileDTO {
    private String sourceFileId;
    private String bucketName;
    private String fileName;
    private String fileMd5;
    private Integer chunkIndex;
    private Integer chunks;

}

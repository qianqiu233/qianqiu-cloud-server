package com.qianqiu.clouddisk.model.dto;

import lombok.Data;

@Data
public class DownloadDTO {
    private String filePath;
    private String signatureUrl;

}

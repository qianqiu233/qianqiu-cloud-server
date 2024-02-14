package com.qianqiu.clouddisk.model.dto;

import lombok.Data;

@Data
public class GetFolderInfoDTO {
    private String path;
    private String shareId;
}

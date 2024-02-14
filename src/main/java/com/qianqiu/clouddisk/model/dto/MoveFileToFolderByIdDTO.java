package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.annotation.ParamCheck;
import lombok.Data;

@Data
public class MoveFileToFolderByIdDTO {
    @ParamCheck
    private String fileId;
    @ParamCheck
    private String filePid;
}

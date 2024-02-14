package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.annotation.ParamCheck;
import lombok.Data;

import java.util.List;
@Data
public class MoveFileListToFolderByIdsDTO {
    @ParamCheck
    private List<String> fileIds;
    @ParamCheck
    private String filePid;

}

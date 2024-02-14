package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.annotation.ParamCheck;
import lombok.Data;

import java.util.List;
@Data
public class SelectAllFolderDTO {
    @ParamCheck
    private String filePid;
//    @ParamCheck
    private String currentFileIds;
}

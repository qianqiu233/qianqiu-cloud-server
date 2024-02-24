package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.annotation.ParamCheck;
import lombok.Data;

@Data
public class SelectSharedFileDTO {
    private Integer pageNum=1;
    private Integer pageSize=15;
    @ParamCheck
    private String shareId;
    @ParamCheck
    private String filePid;

    public SelectSharedFileDTO(Integer pageNum, Integer pageSize, String shareId, String filePid) {
        this.shareId = shareId;
        this.filePid = filePid;
        this.pageNum = pageNum != null ? pageNum : this.pageNum;
        this.pageSize = pageSize != null ? pageSize : this.pageSize;

    }
}

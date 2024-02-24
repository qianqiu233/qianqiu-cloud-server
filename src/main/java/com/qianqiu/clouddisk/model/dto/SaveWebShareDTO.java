package com.qianqiu.clouddisk.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class SaveWebShareDTO {
    //最外头的那个分享
    private String shareId;
    //被选中的文件的fileId
    private List<String> shareFileIds;
    //保存位置的id，默认最外层0
    private String myFolderId;
}

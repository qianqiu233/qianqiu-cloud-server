package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.annotation.ParamCheck;
import com.qianqiu.clouddisk.utils.enums.RegexEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class FileInfoDTO {
    private Integer pageNum=1;
    private Integer pageSize=15;
    @ParamCheck
    private String fileNameFuzzy;
    @ParamCheck
    private String filePid;
    @ParamCheck
    private String category;
    private String fileName;
    private Integer orderBy;
    private Integer sort;

    public FileInfoDTO(Integer pageNum, Integer pageSize, String fileNameFuzzy, String filePid, String category, String fileName, Integer orderBy,Integer sort) {
        this.fileNameFuzzy = fileNameFuzzy;
        this.filePid = filePid;
        this.category = category;
        this.pageNum = pageNum != null ? pageNum : this.pageNum;
        this.pageSize = pageSize != null ? pageSize : this.pageSize;
        this.fileName = fileName;
        this.orderBy = orderBy;
        this.sort = sort;
    }

}

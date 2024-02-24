package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.mbg.mbg_model.FileShare;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
@Data
public class FileShareDTO{
    private String fileName;
    private Long fileSize;
    private String shareId;
    private String fileId;
    private String userId;
    private Integer validType;
    private Date expireTime;
    private Date shareTime;
    private String code;
    private Integer showCount;
    private String sharePath;
    private String webShareUrl;

}

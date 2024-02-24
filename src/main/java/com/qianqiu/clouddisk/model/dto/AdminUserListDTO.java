package com.qianqiu.clouddisk.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserListDTO {
    private String userId;
    private String avatar;
    private String nickName;
    private String email;
    private String createTime;
    private String lastLoginTime;
    private Long useSpace;
    private Long totalSpace;
    private Integer status;



}

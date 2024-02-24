package com.qianqiu.clouddisk.model.dto;

import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import lombok.Data;


public class AdminFileInfoDTO extends FileInfo{
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "AdminFileInfoDTO{" +
                "nickName='" + nickName + '\'' +
                '}';
    }
}

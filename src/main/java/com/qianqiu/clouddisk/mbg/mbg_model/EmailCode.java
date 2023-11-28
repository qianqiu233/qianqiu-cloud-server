package com.qianqiu.clouddisk.mbg.mbg_model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class EmailCode implements Serializable {
    /**
     * 邮箱
     *
     * @mbg.generated
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 编号
     *
     * @mbg.generated
     */
    @Schema(description = "编号")
    private String code;

    /**
     * 创建时间
     *
     * @mbg.generated
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 0:未使用  1:已使用
     *
     * @mbg.generated
     */
    @Schema(description = "0:未使用  1:已使用")
    private Boolean status;

    private static final long serialVersionUID = 1L;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", email=").append(email);
        sb.append(", code=").append(code);
        sb.append(", createTime=").append(createTime);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
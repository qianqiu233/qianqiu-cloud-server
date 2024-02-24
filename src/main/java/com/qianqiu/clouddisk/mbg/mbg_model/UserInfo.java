package com.qianqiu.clouddisk.mbg.mbg_model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Serializable {
    /**
     * 用户ID
     *
     * @mbg.generated
     */
    @Schema(description = "用户ID")
    private String userId;

    /**
     * 昵称
     *
     * @mbg.generated
     */
    @Schema(description = "昵称")
    private String nickName;

    /**
     * 邮箱
     *
     * @mbg.generated
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * qqOpenID
     *
     * @mbg.generated
     */
    @Schema(description = "qqOpenID")
    private String qqOpenId;

    /**
     * qq头像
     *
     * @mbg.generated
     */
    @Schema(description = "qq头像")
    private String qqAvatar;

    /**
     * 密码
     *
     * @mbg.generated
     */
    @Schema(description = "密码")
    private String password;

    /**
     * 创建时间
     *
     * @mbg.generated
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 最后登录时间
     *
     * @mbg.generated
     */
    @Schema(description = "最后登录时间")
    private Date lastLoginTime;

    /**
     * 0:禁用 1:正常
     *
     * @mbg.generated
     */
    @Schema(description = "0:禁用 1:正常")
    private Integer status;

    /**
     * 使用空间单位byte
     *
     * @mbg.generated
     */
    @Schema(description = "使用空间单位byte")
    private Long useSpace;

    /**
     * 总空间
     *
     * @mbg.generated
     */
    @Schema(description = "总空间")
    private Long totalSpace;

    /**
     * 修改时间
     *
     * @mbg.generated
     */
    @Schema(description = "修改时间")
    private Date updateTime;

    /**
     * 用户头像url
     *
     * @mbg.generated
     */
    @Schema(description = "用户头像url")
    private String avatarUrl;

    /**
     * 头像缩小封面
     *
     * @mbg.generated
     */
    @Schema(description = "头像缩小封面")
    private String avatarCover;

    private static final long serialVersionUID = 1L;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQqOpenId() {
        return qqOpenId;
    }

    public void setQqOpenId(String qqOpenId) {
        this.qqOpenId = qqOpenId;
    }

    public String getQqAvatar() {
        return qqAvatar;
    }

    public void setQqAvatar(String qqAvatar) {
        this.qqAvatar = qqAvatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getUseSpace() {
        return useSpace;
    }

    public void setUseSpace(Long useSpace) {
        this.useSpace = useSpace;
    }

    public Long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(Long totalSpace) {
        this.totalSpace = totalSpace;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarCover() {
        return avatarCover;
    }

    public void setAvatarCover(String avatarCover) {
        this.avatarCover = avatarCover;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", nickName=").append(nickName);
        sb.append(", email=").append(email);
        sb.append(", qqOpenId=").append(qqOpenId);
        sb.append(", qqAvatar=").append(qqAvatar);
        sb.append(", password=").append(password);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastLoginTime=").append(lastLoginTime);
        sb.append(", status=").append(status);
        sb.append(", useSpace=").append(useSpace);
        sb.append(", totalSpace=").append(totalSpace);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", avatarUrl=").append(avatarUrl);
        sb.append(", avatarCover=").append(avatarCover);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
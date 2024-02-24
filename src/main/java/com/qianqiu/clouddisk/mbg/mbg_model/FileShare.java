package com.qianqiu.clouddisk.mbg.mbg_model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class FileShare implements Serializable {
    /**
     * 分享ID
     *
     * @mbg.generated
     */
    @Schema(description = "分享ID")
    private String shareId;

    /**
     * 文件ID
     *
     * @mbg.generated
     */
    @Schema(description = "文件ID")
    private String fileId;

    /**
     * 用户ID
     *
     * @mbg.generated
     */
    @Schema(description = "用户ID")
    private String userId;

    /**
     * 有效期类型 0:1天 1:7天 2:30天 3:永久有效
     *
     * @mbg.generated
     */
    @Schema(description = "有效期类型 0:1天 1:7天 2:30天 3:永久有效")
    private Integer validType;

    /**
     * 失效时间
     *
     * @mbg.generated
     */
    @Schema(description = "失效时间")
    private Date expireTime;

    /**
     * 分享时间
     *
     * @mbg.generated
     */
    @Schema(description = "分享时间")
    private Date shareTime;

    /**
     * 提取码
     *
     * @mbg.generated
     */
    @Schema(description = "提取码")
    private String code;

    /**
     * 浏览次数
     *
     * @mbg.generated
     */
    @Schema(description = "浏览次数")
    private Integer showCount;

    /**
     * 分享文件路径
     *
     * @mbg.generated
     */
    @Schema(description = "分享文件路径")
    private String sharePath;

    /**
     * 外部分享
     *
     * @mbg.generated
     */
    @Schema(description = "外部分享")
    private String webShareUrl;

    /**
     * 真正分享链接(服务器授权的)
     *
     * @mbg.generated
     */
    @Schema(description = "真正分享链接(服务器授权的)")
    private String shareUrl;

    private static final long serialVersionUID = 1L;

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getValidType() {
        return validType;
    }

    public void setValidType(Integer validType) {
        this.validType = validType;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getShareTime() {
        return shareTime;
    }

    public void setShareTime(Date shareTime) {
        this.shareTime = shareTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getShowCount() {
        return showCount;
    }

    public void setShowCount(Integer showCount) {
        this.showCount = showCount;
    }

    public String getSharePath() {
        return sharePath;
    }

    public void setSharePath(String sharePath) {
        this.sharePath = sharePath;
    }

    public String getWebShareUrl() {
        return webShareUrl;
    }

    public void setWebShareUrl(String webShareUrl) {
        this.webShareUrl = webShareUrl;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", shareId=").append(shareId);
        sb.append(", fileId=").append(fileId);
        sb.append(", userId=").append(userId);
        sb.append(", validType=").append(validType);
        sb.append(", expireTime=").append(expireTime);
        sb.append(", shareTime=").append(shareTime);
        sb.append(", code=").append(code);
        sb.append(", showCount=").append(showCount);
        sb.append(", sharePath=").append(sharePath);
        sb.append(", webShareUrl=").append(webShareUrl);
        sb.append(", shareUrl=").append(shareUrl);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
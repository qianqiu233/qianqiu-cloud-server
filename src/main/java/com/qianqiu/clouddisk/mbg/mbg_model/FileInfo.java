package com.qianqiu.clouddisk.mbg.mbg_model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class FileInfo implements Serializable {
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
     * md5值，第一次上传记录
     *
     * @mbg.generated
     */
    @Schema(description = "md5值，第一次上传记录")
    private String fileMd5;

    /**
     * 父级ID
     *
     * @mbg.generated
     */
    @Schema(description = "父级ID")
    private String filePid;

    /**
     * 文件大小
     *
     * @mbg.generated
     */
    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 文件名称
     *
     * @mbg.generated
     */
    @Schema(description = "文件名称")
    private String fileName;

    /**
     * 封面
     *
     * @mbg.generated
     */
    @Schema(description = "封面")
    private String fileCover;

    /**
     * 文件路径
     *
     * @mbg.generated
     */
    @Schema(description = "文件路径")
    private String filePath;

    /**
     * 创建时间
     *
     * @mbg.generated
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 最后更新时间
     *
     * @mbg.generated
     */
    @Schema(description = "最后更新时间")
    private Date lastUpdateTime;

    /**
     * 0:文件 1:目录
     *
     * @mbg.generated
     */
    @Schema(description = "0:文件 1:目录")
    private Boolean folderType;

    /**
     * 1:视频 2:音频  3:图片 4:文档 5:其他
     *
     * @mbg.generated
     */
    @Schema(description = "1:视频 2:音频  3:图片 4:文档 5:其他")
    private Boolean fileCategory;

    /**
     *  1:视频 2:音频  3:图片 4:pdf 5:doc 6:excel 7:txt 8:code 9:zip 10:其他
     *
     * @mbg.generated
     */
    @Schema(description = " 1:视频 2:音频  3:图片 4:pdf 5:doc 6:excel 7:txt 8:code 9:zip 10:其他")
    private Boolean fileType;

    /**
     * 0:转码中 1转码失败 2:转码成功
     *
     * @mbg.generated
     */
    @Schema(description = "0:转码中 1转码失败 2:转码成功")
    private Boolean status;

    /**
     * 回收站时间
     *
     * @mbg.generated
     */
    @Schema(description = "回收站时间")
    private Date recoveryTime;

    /**
     * 删除标记 0:删除  1:回收站  2:正常
     *
     * @mbg.generated
     */
    @Schema(description = "删除标记 0:删除  1:回收站  2:正常")
    private Boolean delFlag;

    private static final long serialVersionUID = 1L;

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

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFilePid() {
        return filePid;
    }

    public void setFilePid(String filePid) {
        this.filePid = filePid;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileCover() {
        return fileCover;
    }

    public void setFileCover(String fileCover) {
        this.fileCover = fileCover;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Boolean getFolderType() {
        return folderType;
    }

    public void setFolderType(Boolean folderType) {
        this.folderType = folderType;
    }

    public Boolean getFileCategory() {
        return fileCategory;
    }

    public void setFileCategory(Boolean fileCategory) {
        this.fileCategory = fileCategory;
    }

    public Boolean getFileType() {
        return fileType;
    }

    public void setFileType(Boolean fileType) {
        this.fileType = fileType;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getRecoveryTime() {
        return recoveryTime;
    }

    public void setRecoveryTime(Date recoveryTime) {
        this.recoveryTime = recoveryTime;
    }

    public Boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", fileId=").append(fileId);
        sb.append(", userId=").append(userId);
        sb.append(", fileMd5=").append(fileMd5);
        sb.append(", filePid=").append(filePid);
        sb.append(", fileSize=").append(fileSize);
        sb.append(", fileName=").append(fileName);
        sb.append(", fileCover=").append(fileCover);
        sb.append(", filePath=").append(filePath);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append(", folderType=").append(folderType);
        sb.append(", fileCategory=").append(fileCategory);
        sb.append(", fileType=").append(fileType);
        sb.append(", status=").append(status);
        sb.append(", recoveryTime=").append(recoveryTime);
        sb.append(", delFlag=").append(delFlag);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
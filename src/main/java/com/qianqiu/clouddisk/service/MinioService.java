package com.qianqiu.clouddisk.service;

import com.qianqiu.clouddisk.mbg.mbg_model.FileShare;
import com.qianqiu.clouddisk.model.dto.InitSliceUploadFileDTO;
import com.qianqiu.clouddisk.model.dto.MinioUploadDTO;
import com.qianqiu.clouddisk.utils.enums.ShareValidTypeEnums;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MinioService {
    /**
     * 上传头像
     *
     * @param file
     * @param bucketName
     * @return
     */
    MinioUploadDTO uploadAvatar(MultipartFile file, String bucketName);

//    /**
//     * 分片文件上传初始化
//     * @return
//     */
////    Boolean initMultipartUpload();

    /**
     * 上传文件
     *
     * @param file
     * @param bucketName
     * @return
     */
    MinioUploadDTO uploadFile(MultipartFile file, String bucketName);

    MinioUploadDTO uploadSliceFile(MultipartFile file, String bucketName, String fileId, Integer chunkIndex, String fileName, Integer chunks);

    void initSliceUploadFile(InitSliceUploadFileDTO initSliceUploadFileDTO);

    MinioUploadDTO composeFile(String bucketName, String chunkObjectName, String fullObjectName, Integer chunks,String objectType);

    Boolean delSliceFile(String bucketName, Integer chunks,String chunkObjectName);
    Boolean delFiles(String bucketName, List<String> delObjectList);
    MinioUploadDTO createFolder(String folderName, String bucketName,String filePid);
    FileShare fileShare(String bucketName, String objectName, ShareValidTypeEnums time, String shareCode);
    boolean cancelShareList(String bucketName, List<String> objectNameList, Integer time);
    String uploadThumbnail(String localFilePath, String bucketName,String objectName);

    boolean cancelShareListMoreThan30(List<String> filePathListMoreThan30);
    String copyBucketItem2OtherBucket(String sourceBucketName,String sourceObjectName,String targetBucketName,String targetObjectName);
}

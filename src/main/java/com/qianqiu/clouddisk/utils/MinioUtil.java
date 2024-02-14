package com.qianqiu.clouddisk.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.qianqiu.clouddisk.exception.CommonException;
import com.qianqiu.clouddisk.model.dto.BucketPolicyConfigDTO;
import com.qianqiu.clouddisk.model.dto.MinioUploadDTO;
import com.qianqiu.clouddisk.utils.commonResult.ResultCode;
import com.qianqiu.clouddisk.utils.enums.FileCategoryEnums;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.DEFAULT_AVATAR_PACKAGE;

@Component
@Slf4j
public class MinioUtil{
    @Value("${minio.endpoint}")
    private String ENDPOINT;
    @Autowired
    private MinioClient minioClient;
    private static Logger logger = LoggerFactory.getLogger(MinioUtil.class);

    /**
     * 文件上传核心
     *
     * @param file
     * @param bucketName
     * @param objectName
     */
    public Boolean uploadCore(MultipartFile file, String bucketName, String objectName) {
        try {
            long fileSize = file.getSize();
            // PutObjectOptions，上传配置(文件大小，内存中文件分片大小)
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    //对象名称
                    .object(objectName)
                    //文件类型
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), fileSize, ObjectWriteArgs.MIN_MULTIPART_SIZE).build();
            //上传
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            log.info("上传文件失败{}", objectName);
            return false;
        }
        log.info("存储路径{}", objectName);
        return true;

    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public MinioUploadDTO uploadFile(MultipartFile file, String bucketName){
        createBucket(bucketName);
        // 上传文件的名称
        String fileName = file.getOriginalFilename();
        //按日分包
        Date date = new Date();
        String dateYMD = DateUtil.format(date, DateUtil.YMD);
        //按时间再分包
        String dateHMS = DateUtil.format(date, DateUtil.HMS);
        //判断类型，我该丢哪个包
        FileCategoryEnums fileType = FileAboutUtil.getFileCategoryType(file);
        //存储对象名称 2002255525/fileName
        String filePath = "/" + dateYMD + "/" + dateHMS + "/";
        String objectPath = fileType.getPackageName() + filePath;
        String objectName = objectPath + fileName;
        long fileSize = file.getSize();
        //       上传
        uploadCore(file, bucketName, objectName);
        String AccessPath = ENDPOINT + bucketName + objectName;
        MinioUploadDTO minioUploadDto = MinioUploadDTO.builder()
                .fileUrl(AccessPath)
                .fileName(fileName)
                .fileSize(fileSize)
                .filePath(objectPath)
                .fileCategory(fileType.getCategory())
                .createTime(date)
                .lastUpdateTime(date)
                .fileContentType(file.getContentType())
                .build();
        log.info("文件已经上传,访问路径{}", AccessPath);
        // 返回访问路径
        return minioUploadDto;
    }


    /**
     * 创建桶
     *
     * @param bucketName
     * @throws Exception
     */
    public void createBucket(String bucketName) {
        try {
            boolean isExist = bucketExists(bucketName);
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                BucketPolicyConfigDTO bucketPolicyConfigDto = createBucketPolicyConfigDto(bucketName);
                SetBucketPolicyArgs setBucketPolicyArgs = SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(JSONUtil.toJsonStr(bucketPolicyConfigDto))
                        .build();
                minioClient.setBucketPolicy(setBucketPolicyArgs);
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     * 创建桶访问策略
     *
     * @param bucketName
     * @return
     */
    private BucketPolicyConfigDTO createBucketPolicyConfigDto(String bucketName) {
        BucketPolicyConfigDTO.Statement statement = BucketPolicyConfigDTO.Statement.builder()
                .Effect("Allow")
                .Principal("*")
                .Action("s3:GetObject")
                .Resource("arn:aws:s3:::" + bucketName + "/*.**").build();
        return BucketPolicyConfigDTO.builder()
                .Version("2024-1-1")
                .Statement(CollUtil.toList(statement))
                .build();
    }

    /**
     * 获取所有存储桶名称
     *
     * @return
     */
    public List<String> listBucketNames() throws Exception {
        List<Bucket> bucketList = listBuckets();
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        return bucketListName;
    }

    /**
     * 查看所有桶
     *
     * @return
     * @throws Exception
     */
    public List<Bucket> listBuckets() throws Exception {
        return minioClient.listBuckets();
    }

    /**
     * 检查存储桶是否存在
     *
     * @param bucketName
     * @return
     * @throws Exception
     */
    public boolean bucketExists(String bucketName) {
        boolean isExist;
        try {
            isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new CommonException("桶出现错误" + e.toString());
        }
        return isExist;

    }

    /**
     * 删除桶
     *
     * @param bucketName
     * @return
     * @throws Exception
     */
    public boolean removeBucket(String bucketName) throws Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            boolean emptyBucket = isEmptyBucket(bucketName);
            // 只有存储桶为空时才能删除成功。
            if (emptyBucket) {
                minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            }
            //是不是真的删掉了
            flag = bucketExists(bucketName);
            //false成功删除了
            if (!flag) {
                return true;
            }

        }
        return false;
    }

    /**
     * 列出存储桶中的所有对象
     *
     * @param bucketName 存储桶名称
     * @return
     * @throws Exception
     */
    public Iterable<Result<Item>> listObjects(String bucketName, boolean enableRecursive) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            ListObjectsArgs.Builder builder = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .recursive(enableRecursive);
            return minioClient.listObjects(builder.build());
        }
        return null;
    }

    public Iterable<Result<Item>> listObjects(String bucketName, boolean enableRecursive, String category) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            ListObjectsArgs.Builder builder = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(category)
                    .recursive(enableRecursive);
            return minioClient.listObjects(builder.build());
        }
        return null;
    }

    /**
     * 判断桶是否是空的
     *
     * @param bucketName
     * @return
     */
    public boolean isEmptyBucket(String bucketName) throws Exception {
        if (listObjects(bucketName, true) == null) {
            return true;
        }
        return false;
    }

    public MinioUploadDTO createFolder(String folderName, String bucketName) {
        log.info("创建分片目录中|参数|folderName:{}|bucketName:{}",folderName,bucketName);
        MinioUploadDTO minioUploadDTO = null;
        try {
            // 在对象键中使用斜杠来模拟文件夹
            // 检查文件夹是否已存在
            if (!doesFolderExist(bucketName,folderName)) {
                String objectName = folderName + "/";
                // 创建 PutObjectArgs 对象，传递一个空的输入流和大小参数
                PutObjectArgs build = PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                        .build();
                // 使用 minioClient.putObject 上传对象
                minioClient.putObject(build);
                String objectNameUrl=ENDPOINT+bucketName+"/"+objectName;
                minioUploadDTO= MinioUploadDTO.builder()
                        .fileUrl(objectNameUrl)
                        .fileName(folderName)
                        .createTime(new Date())
                        .lastUpdateTime(new Date())
                        .filePath(objectName)
                        .build();
            }
        } catch (Exception e) {
            throw new CommonException("创建文件夹失败" + e);
        }
        return minioUploadDTO;
    }
    /**
     * 判断文件夹是否存在
     *
     * @param bucketName 存储桶
     * @param objectName 文件夹名称（去掉/）
     * @return true：存在
     */
    public boolean doesFolderExist(String bucketName, String objectName) {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).prefix(objectName).recursive(false).build());
            objectName=objectName+"/";
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir() && objectName.equals(item.objectName())) {
                    logger.warn("该文件夹已经存在:{}--",bucketName+"/"+objectName);
                    return true; // 文件夹存在，提前返回 true
                }
            }
        } catch (Exception e) {
            return false;
            // 在异常发生时返回 false
        }
        return false; // 文件夹不存在
    }

    /**
     * 文件合并
     *
     * @param bucketName       桶名称
     * @param objectName       完整对象名称
     * @param sourceObjectList 源文件分片数据
     */
    public Boolean composeFile(String bucketName, String objectName, List<ComposeSource> sourceObjectList) {
        // 合并操作
        log.info("文件集,合并开始|composeFile|参数|bucketName:{}|objectName:{}|sourceObjectList:{}",bucketName, objectName,sourceObjectList);
        try {
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .sources(sourceObjectList)
                            .build());
            return true;
        } catch (Exception e) {
            log.error("Minio文件按合并异常!|参数：bucketName:{},objectName:{}|sourceObjectList:{},异常:{}", bucketName, objectName,sourceObjectList, e);
            return false;
        }
    }
    /**
     * 多个文件删除
     *
     * @param bucketName 桶名称
     */
    public Boolean removeFiles(String bucketName, List<DeleteObject> delObjects) {
        log.info("多文件删除|参数|bucketName:{}|delObjects:{}",bucketName,delObjects);
        try {
            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(
                            RemoveObjectsArgs.builder().bucket(bucketName).objects(delObjects).build());
            boolean isFlag = true;
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("删除对象时出错 {} | {}", error.objectName(), error.message());
                isFlag = false;
            }
            log.info("多文件删除|结果|res:{}",isFlag);
            return isFlag;
        } catch (Exception e) {
            log.error("Minio多个文件删除异常!|参数：bucketName:{}|异常:{}", bucketName,e.toString());
            return false;
        }
    }
    public Boolean existsObject(String bucketName,String objectName){
        log.info("判断文件是否存在|参数|bucketName:{}|objectName:{}",bucketName,objectName);
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName).build());
        } catch (Exception e) {
            log.info("判断文件是否存在|结果|res:false");
            return false;
        }
        log.info("判断文件是否存在|结果|res:true");
        return true;
    }





}

package com.qianqiu.clouddisk.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.qianqiu.clouddisk.exception.CommonException;
import com.qianqiu.clouddisk.model.dto.BucketPolicyConfigDTO;
import com.qianqiu.clouddisk.model.dto.MinioUploadDTO;
import com.qianqiu.clouddisk.utils.commonResult.ResultCode;
import io.minio.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.qianqiu.clouddisk.utils.Constant.FileConstant.*;

@Component
@Slf4j
public class MinioUtil implements InitializingBean {
    @Value("${minio.endpoint}")
    private String ENDPOINT;
    @Value("${minio.accessKey}")
    private String ACCESS_KEY;
    @Value("${minio.secretKey}")
    private String SECRET_KEY;
    private MinioClient minioClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(ACCESS_KEY, "Minio accessKey为空");
        Assert.hasText(SECRET_KEY, "Minio secretKey为空");
        this.minioClient = MinioClient.builder()
                .endpoint(ENDPOINT)
                .credentials(ACCESS_KEY, SECRET_KEY)
                .build();
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public MinioUploadDTO uploadFile(MultipartFile file, String bucketName) {
        createBucket(bucketName);
        // 上传文件的名称
        String fileName = file.getOriginalFilename();
        //按日分包
        Date date = new Date();
        SimpleDateFormat dayPackage = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat NameByTime = new SimpleDateFormat("HHmmssSSS");
        // todo 需要从当天的包里，查找是否存在名字一样的文件，存在，判断是否相同，相同则修改之前的文件名（删除？再添加）
        //判断类型，我该丢哪个包
        String fileType = getFileType(file) + "/";
        //存储对象名称 2002255525/fileName
        String filePath = dayPackage.format(date) + "/";
        String fileTimeName=NameByTime.format(date)+fileName;
        String objectName = fileType + filePath + fileTimeName;
        try {
            // PutObjectOptions，上传配置(文件大小，内存中文件分片大小)
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    //对象名称
                    .object(objectName)
                    //文件类型
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), ObjectWriteArgs.MIN_MULTIPART_SIZE).build();
            //上传
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            throw new CommonException(ResultCode.FAILED.getCode(),"上传失败");
        }
        log.info("对象存储路径{}", objectName);
        MinioUploadDTO minioUploadDto = new MinioUploadDTO();
        minioUploadDto.setFileName(fileName);
        String AccessPath = ENDPOINT + "/" + bucketName + "/" + objectName;
        minioUploadDto.setFileUrl(AccessPath);
        log.info("文件已经上传,访问路径{}", AccessPath);
        // 返回访问路径
        return minioUploadDto;
    }

    /**
     * 获取匹配文件类型
     * @param file
     * @return
     */
    public String getFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (StrUtil.isBlank(contentType)) {
            return null;
        }
        if (contentType.startsWith("application") || contentType.startsWith("text")) {
            return FILE_PACKAGE;
        }
        if (contentType.startsWith("video")) {
            return VIDEO_PACKAGE;
        }
        if (contentType.startsWith("audio")) {
            return AVATAR_PACKAGE;
        }
        if (contentType.startsWith("image")) {
            return IMAGE_PACKAGE;
        }
        return OTHER_PACKAGE;
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
                .Version("2012-10-17")
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
    public boolean bucketExists(String bucketName) throws Exception {
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (isExist) {
            return true;
        }
        return false;
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
    public Iterable<Result<Item>> listObjects(String bucketName) throws Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
            return results;
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
        if (listObjects(bucketName) == null) {
            return true;
        }
        return false;
    }


}

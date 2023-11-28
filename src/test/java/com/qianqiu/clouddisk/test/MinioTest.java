package com.qianqiu.clouddisk.test;

import com.qianqiu.clouddisk.exception.CommonException;
import com.qianqiu.clouddisk.model.dto.MinioUploadDTO;
import com.qianqiu.clouddisk.utils.MinioUtil;
import com.qianqiu.clouddisk.utils.commonResult.ResultCode;
import io.minio.messages.Bucket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class MinioTest {
    @Autowired
    private MinioUtil minioUtil;

    @Test
    void uploadTest() throws Exception {
        // 提供文件路径
        String filePath = "C:\\Users\\27996\\Documents\\WeChat Files\\wxid_zbry36ae9d8o22\\FileStorage\\File\\2023-11\\request\\request\\type.ts";
        File file=new File(filePath);
        MultipartFile multipartFile=new MockMultipartFile(
                "file",
                file.getName(),
                "text/typescript",
                new FileInputStream(file)

        );
        System.out.println(multipartFile.getContentType());
        MinioUploadDTO minioUploadDto = minioUtil.uploadFile(multipartFile, "test");
        System.out.println(minioUploadDto);

    }
    @Test
    void removeTest(){
        String bucketName="test";
        try {
            List<String> strings = minioUtil.listBucketNames();
            System.out.println(strings);
            System.out.println(minioUtil.isEmptyBucket(bucketName));
            for (Bucket listBucket : minioUtil.listBuckets()) {
                System.out.println(listBucket);
            }
            System.out.println(minioUtil.removeBucket(bucketName));
        } catch (Exception e) {
            throw new CommonException(ResultCode.FAILED);
        }

    }
    @Test
    void test(){
        SimpleDateFormat NameByTime = new SimpleDateFormat("HHmmssSSS");
        String format = NameByTime.format(new Date());
        //存储对象名称 2002255525/fileName
        System.out.println(format);
    }
}

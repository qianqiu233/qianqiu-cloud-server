package com.qianqiu.clouddisk.test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qianqiu.clouddisk.exception.CommonException;
import com.qianqiu.clouddisk.model.dto.MinioUploadDTO;
import com.qianqiu.clouddisk.utils.MinioUtil;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import com.qianqiu.clouddisk.utils.commonResult.ResultCode;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import io.minio.messages.Owner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class MinioTest {
    @Autowired
    private MinioUtil minioUtil;

    @Test
    void uploadTest() throws Exception {
        // 提供文件路径
        String filePath = "C:\\Users\\27996\\Documents\\WeChat Files\\wxid_zbry36ae9d8o22\\FileStorage\\File\\2023-11\\request\\request\\type.ts";
        File file = new File(filePath);
        MultipartFile multipartFile = new MockMultipartFile(
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
    void removeTest() {
        String bucketName = "test";
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
    void listObjectsTest() throws Exception {
        String bucketName = "test";
        Iterable<Result<Item>> results = minioUtil.listObjects(bucketName, true,"file");
        Iterator<Result<Item>> iterator = results.iterator();
        ArrayList<Item> items = new ArrayList<>();
        while (iterator.hasNext()) {
            Result<Item> next = iterator.next();
            Item item = next.get();
            System.out.println(item.objectName());
            items.add(item);
        }
        System.out.println(CommonResult.success(CommonPage.restPage(items)));
    }

    @Test
    void listObjectsByPageTest() throws Exception {
        // 获取的元数据信息包括以下字段：
        // 1. 对象的大小：item.size +
        // 2. 对象的 ETag：item.etag
        // 3. 对象的最后修改时间：item.lastModified +
        // 4. 对象的存储类别：item.storageClass
        // 6. 对象的桶名：item.bucketName
        // 7. 对象的名称：item.objectName +
        // 8. 对象的用户定义元数据：item.userMetadata
        // 9. 对象的标签：item.tags
        //10.item.owner();对象所有者信息
        String bucketName = "test";
        Iterable<Result<Item>> results = minioUtil.listObjects(bucketName, true);
        Iterator<Result<Item>> iterator = results.iterator();

        ArrayList<Item> items = new ArrayList<>();
        while (iterator.hasNext()) {
            Result<Item> next = iterator.next();
            Item item = next.get();
            ZonedDateTime s = item.lastModified();
            Owner owner = item.owner();
            System.out.println(owner.id()+"--"+owner.displayName()+"----"+item.objectName());
            items.add(item);
        }
//        CommonPage<Item> itemCommonPage = CommonPage.setPage(items, 0, 6);
//        List<Item> list = itemCommonPage.getList();
//        for (Item item : list) {
//            System.out.println(item.objectName());
//        }
    }
    @Test
    void testCreateFolder(){
        minioUtil.createFolder("test","f85966923c7f4ffea07121ed2e9cb7c0");
    }
    @Test
    void testFolderExist(){
        boolean b = minioUtil.doesFolderExist("f85966923c7f4ffea07121ed2e9cb7c0", "test");
        System.out.println(b);
    }

}

package com.qianqiu.clouddisk.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.qianqiu.clouddisk.exception.CommonException;
import com.qianqiu.clouddisk.mbg.mbg_mapper.FileInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfoExample;
import com.qianqiu.clouddisk.model.dto.InitSliceUploadFileDTO;
import com.qianqiu.clouddisk.model.dto.MinioUploadDTO;
import com.qianqiu.clouddisk.service.MinioService;
import com.qianqiu.clouddisk.utils.DateUtil;
import com.qianqiu.clouddisk.utils.FileAboutUtil;
import com.qianqiu.clouddisk.utils.MinioUtil;
import com.qianqiu.clouddisk.utils.enums.FileCategoryEnums;
import io.minio.ComposeSource;
import io.minio.messages.DeleteObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.DEFAULT_AVATAR_PACKAGE;
@Slf4j
@Service
public class MinioServiceImpl implements MinioService {
    @Autowired
    private MinioUtil minioUtil;
    @Value("${minio.endpoint}")
    private String ENDPOINT;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private FileInfoMapper fileInfoMapper;


    @Override
    public MinioUploadDTO uploadAvatar(MultipartFile file, String bucketName) {
        minioUtil.createBucket(bucketName);
        Date date = new Date();
        // 上传文件的名称
        String fileName = file.getOriginalFilename();
        String objectPath = DEFAULT_AVATAR_PACKAGE;
        String objectName = objectPath + "/" + fileName;
        long fileSize = file.getSize();
//       上传
        Boolean isUpLoad = minioUtil.uploadCore(file, bucketName, objectName);
        if (!isUpLoad){
            throw new CommonException("头像上传失败");
        }
        String AccessPath = ENDPOINT + bucketName + objectName;
        MinioUploadDTO minioUploadDto = MinioUploadDTO.builder()
                .fileUrl(AccessPath)
                .fileName(fileName)
                .fileSize(fileSize)
                .filePath(objectPath)
                .fileCategory(FileCategoryEnums.IMAGE.getCategory())
                .createTime(date)
                .lastUpdateTime(date)
                .fileContentType(file.getContentType())
                .build();
        log.info("头像已经上传,访问路径{}", AccessPath);
        return minioUploadDto;
    }

    @Override
    public MinioUploadDTO uploadFile(MultipartFile file, String bucketName) {
        minioUtil.createBucket(bucketName);
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
        Boolean isUpLoad = minioUtil.uploadCore(file, bucketName, objectName);
        if (!isUpLoad){
            throw new CommonException("文件上传失败");
        }
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

    @Override
    public MinioUploadDTO uploadSliceFile(MultipartFile file, String bucketName, String fileId, Integer chunkIndex, String fileName, Integer chunks) {
        minioUtil.createBucket(bucketName);
        // 后缀
        String suffix = "."+FileUtil.extName(fileName);
        //文件名
        String chunkFileName = FileUtil.mainName(fileName);
        String chunkObjectName=fileId+"-"+fileName;
        fileName=fileId+"-"+chunkFileName+"-"+chunkIndex+suffix;
        String objectName = "/slice/"+fileName;
        FileCategoryEnums fileType = FileAboutUtil.getFileCategoryType(file);
        Boolean isUpLoad = minioUtil.uploadCore(file, bucketName, objectName);
        if (!isUpLoad){
            delSliceFile(bucketName, chunks, chunkObjectName);
            //清空redis数据
            stringRedisTemplate.delete(fileId);
            throw new CommonException("分片上传失败，为保证文件完整，正在删除所有分片");
        }
        String slicePath = ENDPOINT + bucketName + objectName;
        MinioUploadDTO minioUploadDto = MinioUploadDTO.builder()
                .fileUrl(slicePath)
                .fileName(chunkObjectName)
                .fileSize(file.getSize())
                .filePath(objectName)
                .fileCategory(fileType.getCategory())
                .createTime(new Date())
                .lastUpdateTime(new Date())
                .fileContentType(file.getContentType())
                .build();
        if (chunkIndex>=chunks-1){
            minioUploadDto.setUploadFlag(-1);
        }else {
            minioUploadDto.setUploadFlag(-4);
        }
        log.info("minioUploadDto相关信息:{}",minioUploadDto);
        log.info("文件:{}第-{}-分片上传成功，路径为:{}",fileName,chunkIndex,slicePath);
        return minioUploadDto;
    }

    @Override
    public void initSliceUploadFile(InitSliceUploadFileDTO initSliceUploadFileDTO) {
        //先创建包，
        MinioUploadDTO minioUtilFolder = minioUtil.createFolder("slice", initSliceUploadFileDTO.getBucketName());
        String sourceFileId = initSliceUploadFileDTO.getSourceFileId();
        //初始化切片放入缓存
        Boolean hasKey= stringRedisTemplate.hasKey(sourceFileId);
        if (Boolean.FALSE.equals(hasKey)){
            for (int i = 0; i < initSliceUploadFileDTO.getChunks(); i++) {
                stringRedisTemplate.opsForList().rightPush(sourceFileId, String.valueOf(i));
            }
        }
        //存入数据库
        //判断库中是否存在该文件（源id）
        FileInfoExample fileInfoExample = new FileInfoExample();
        fileInfoExample.createCriteria().andFileIdEqualTo(sourceFileId);
        long count = fileInfoMapper.countByExample(fileInfoExample);
        if (count==0){
            //文件不存在
        }
    }

    /**
     * @param bucketName      桶名
     * @param chunkObjectName 分片名
     * @param fullObjectName  完整文件名
     * @param chunks          分片数量
     * @return
     */
    @Override
    public MinioUploadDTO composeFile(String bucketName, String chunkObjectName, String fullObjectName, Integer chunks,String objectType){
        log.info("文件合并开始,获取分片集");
        String[] strings = FileAboutUtil.splitByLastDot(chunkObjectName);
        //文件名
        String chunkFileName = strings[0];
        String suffix = strings[1];
        // 创建一个包含分片对象信息的列表
        List<ComposeSource> composeSourceList = new ArrayList<>();
        for (int i = 0; i < chunks; i++) {
            chunkObjectName="/slice/"+chunkFileName+"-"+i+suffix;
            ComposeSource source = ComposeSource.builder()
                    .bucket(bucketName)
                    .object(chunkObjectName)
                    .build();
            composeSourceList.add(source);
            log.info("文件合并中,分片参数|第{}分片|bucket:{}|object:{}",i,bucketName ,chunkObjectName);
        }
        // 输出调试日志，记录方法参数
        log.info("完整分片集|composeFile|参数bucketName:{}, fullObjectName:{}, composeSourceList:{}", bucketName, fullObjectName, composeSourceList);
        // todo 拼接文件路径对象名称 先给放在测试文件夹
        String realObjectName=fullObjectName;
        String realObjectPath=objectType+"/"+fullObjectName;
        Boolean isCompose = minioUtil.composeFile(bucketName, realObjectPath, composeSourceList);
        if (!isCompose){
            throw new CommonException("合并文件出现错误，请重试");
        }
        String sourceFilePath = ENDPOINT + bucketName+"/" + realObjectPath;
        MinioUploadDTO minioUploadDTO = MinioUploadDTO.builder()
                .filePath(realObjectPath)
                .fileUrl(sourceFilePath)
                .fileName(realObjectName)
                .uploadFlag(-2).build();
        return minioUploadDTO;

    }

    @Override
    public Boolean delSliceFile(String bucketName , Integer chunks,String chunkObjectName) {
        // 删除所有的临时分片文件
        log.info("删除文件分片|获取分片集|参数|bucketName:{}|chunks:{}|chunkObjectName:{}|",bucketName,chunks,chunkObjectName);
        List<DeleteObject> delObjects = new ArrayList<>();
        String[] strings = FileAboutUtil.splitByLastDot(chunkObjectName);
        //文件名
        String chunkFileName = strings[0];
        String suffix = strings[1];
        for (int i = 0; i < chunks; i++) {
            String deleteObjectName = "/slice/"+chunkFileName+"-"+i+suffix;
            Boolean existsObject = minioUtil.existsObject(bucketName, deleteObjectName);
            if (existsObject){
                log.info("删除文件分片|获取分片集|添加deleteObjectName:{}",deleteObjectName);
                delObjects.add(new DeleteObject(deleteObjectName));
            }
        }
        return minioUtil.removeFiles(bucketName,delObjects);
    }

    @Override
    public Boolean delFiles(String bucketName, List<String> delObjectList) {
        log.info("批量删除文件|获取删除集|参数|bucketName:{}|delObjectList:{}",bucketName,delObjectList);
        List<DeleteObject> delObjects = new ArrayList<>();
        for (String delObject : delObjectList) {
            Boolean existsObject = minioUtil.existsObject(bucketName, delObject);
            if (existsObject){
                log.info("批量删除文件|获取删除集|添加delObject:{}",delObject);
                delObjects.add(new DeleteObject(delObject));
            }
        }
        return minioUtil.removeFiles(bucketName,delObjects);
    }
}
